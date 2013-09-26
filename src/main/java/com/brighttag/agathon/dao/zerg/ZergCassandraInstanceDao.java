package com.brighttag.agathon.dao.zerg;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.ning.http.client.AsyncHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;

/**
 * Zerg implementation of {@link CassandraInstanceDAO}.
 *
 * By convention, a Zerg host is considered to be part of a Cassandra ring if it contains
 * a role named "cassandra_<ringname>".
 *
 * Because Zerg uniquely identifies instances by hostname (which remain stable even if rebuilt),
 * this DAO uses the {@link String#hashCode() hashCode} of the hostname as the Cassandra instance ID.
 *
 * Finally, the Zerg DAO assumes you're running Cassandra on EC2 with the {@link Ec2MultiRegionSnitch}.
 * Therefore it translates the EC2 region ("us-east-1") and availability zone ("a") into the Cassandra
 * data center ("us-east") and rack ("1a"), as expected by this particular snitch.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergCassandraInstanceDao implements CassandraInstanceDao {

  private static final Logger LOG = LoggerFactory.getLogger(ZergCassandraInstanceDao.class);

  private final AsyncHttpClient client;
  private final String manifestUrl;
  private final Gson gson;

  @Inject
  public ZergCassandraInstanceDao(AsyncHttpClient client, Gson gson,
      @Named(ZergDaoModule.ZERG_MANIFEST_URL_PROPERTY) String manifestUrl) {
    this.client = client;
    this.gson = gson;
    this.manifestUrl = manifestUrl;
  }

  @Override
  public ImmutableSet<CassandraInstance> findAll(String ring) {
    Set<CassandraInstance> instances = Sets.newHashSet();
    for (Map.Entry<String, Map<String, Host>> regionEntry : getRegions().entrySet()) {
      Map<String, Host> region = regionEntry.getValue();
      for (Map.Entry<String, Host> hostEntry : region.entrySet()) {
        Host host = hostEntry.getValue();
        if (host.roles.contains(role(ring))) {
          instances.add(instance(hostEntry.getKey(), host));
        }
      }
    }
    return ImmutableSet.copyOf(instances);
  }

  @Override
  public @Nullable CassandraInstance findById(String ring, int id) {
    for (Map.Entry<String, Map<String, Host>> regionEntry : getRegions().entrySet()) {
      Map<String, Host> region = regionEntry.getValue();
      for (Map.Entry<String, Host> hostEntry : region.entrySet()) {
        String hostname = hostEntry.getKey();
        Host host = hostEntry.getValue();
        if (host.roles.contains(role(ring))) {
          int hostId = hostId(hostname);
          if (hostId == id) {
            return instance(hostname, host);
          }
        }
      }
    }
    return null;
  }

  @Override
  public void save(String ring, CassandraInstance instance) {
    throw new UnsupportedOperationException("Save is not supported for " + getClass().getSimpleName());
  }

  @Override
  public void delete(String ring, CassandraInstance instance) {
    throw new UnsupportedOperationException("Delete is not supported for " + getClass().getSimpleName());
  }

  Map<String, Map<String, Host>> getRegions() {
    try {
      Map<String, Map<String, Host>> regions = gson.fromJson(execute(manifestUrl), MAP_OF_REGIONS.getType());
      return Objects.firstNonNull(regions, ImmutableMap.<String, Map<String, Host>>of());
    } catch (JsonSyntaxException e) {
      LOG.warn("Received bad JSON from Zerg {}: {}", manifestUrl, e);
    }
    return ImmutableMap.of();
  }

  private int hostId(String hostname) {
    return hostname.hashCode();
  }

  private String role(String ring) {
    return "cassandra_" + ring;
  }

  private CassandraInstance instance(String hostname, Host host) {
    int lastDash = host.zone.lastIndexOf("-");
    String dataCenter = host.zone.substring(0, lastDash);
    String rack = host.zone.substring(lastDash + 1);
    return new CassandraInstance.Builder()
        .id(hostId(hostname))
        .dataCenter(dataCenter)
        .rack(rack)
        .hostName(hostname)
        .publicIpAddress(host.publicIp)
        .build();
  }

  private static final TypeLiteral<Map<String, Map<String, Host>>> MAP_OF_REGIONS =
      new TypeLiteral<Map<String, Map<String, Host>>>() { };

  private @Nullable String execute(String url) {
    try {
      return client.prepareGet(url).execute().get().getResponseBody();
    } catch (IOException e) {
      LOG.warn("Unable to fetch manifest from zerg url: {}", url, e);
    } catch (InterruptedException e) {
      LOG.warn("Interrupted while fetching manifest from zerg", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      LOG.warn("Caught exception fetching manifest from zerg {}", url, e);
    }
    return null;
  }

  /**
   * Helper class to deserialize Host JSON.
   */
  static class Host {
    @SerializedName("public ip") String publicIp;
    List<String> roles;
    String zone;
  }

}

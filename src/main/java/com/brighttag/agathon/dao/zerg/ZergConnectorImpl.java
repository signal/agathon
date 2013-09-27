package com.brighttag.agathon.dao.zerg;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.ning.http.client.AsyncHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codyaray
 * @since 9/27/2013
 */
class ZergConnectorImpl implements ZergConnector {

  private static final Logger LOG = LoggerFactory.getLogger(ZergConnectorImpl.class);

  private final AsyncHttpClient client;
  private final String manifestUrl;
  private final Gson gson;

  @Inject
  public ZergConnectorImpl(AsyncHttpClient client, Gson gson,
      @Named(ZergDaoModule.ZERG_MANIFEST_URL_PROPERTY) String manifestUrl) {
    this.client = client;
    this.gson = gson;
    this.manifestUrl = manifestUrl;
  }

  @Override
  public ImmutableSet<ZergHost> getHosts() {
    ImmutableSet.Builder<ZergHost> hosts = ImmutableSet.builder();
    for (Map.Entry<String, Map<String, ZergHost>> regionEntry : getRegions().entrySet()) {
      Map<String, ZergHost> region = regionEntry.getValue();
      for (Map.Entry<String, ZergHost> hostEntry : region.entrySet()) {
        ZergHost host = hostEntry.getValue();
        host.setName(hostEntry.getKey());
        hosts.add(host);
      }
    }
    return hosts.build();
  }

  private Map<String, Map<String, ZergHost>> getRegions() {
    try {
      Map<String, Map<String, ZergHost>> regions = gson.fromJson(
          execute(manifestUrl), MAP_OF_REGIONS.getType());
      return Objects.firstNonNull(regions, ImmutableMap.<String, Map<String, ZergHost>>of());
    } catch (JsonSyntaxException e) {
      LOG.warn("Received bad JSON from Zerg {}: {}", manifestUrl, e);
    }
    return ImmutableMap.of();
  }

  private static final TypeLiteral<Map<String, Map<String, ZergHost>>> MAP_OF_REGIONS =
      new TypeLiteral<Map<String, Map<String, ZergHost>>>() { };

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

}

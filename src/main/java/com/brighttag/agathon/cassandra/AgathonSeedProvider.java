package com.brighttag.agathon.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.locator.SeedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cassandra seed provider that requests the seed list from Agathon.
 *
 * @author codyaray
 * @since 5/23/12
 */
public class AgathonSeedProvider implements SeedProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AgathonSeedProvider.class);

  private final AgathonConnector connector;

  /**
   * Creates a new seed provider with {@code args} populated from {@code cassandra.yaml}.
   * @param unused unused args
   */
  @SuppressWarnings("PMD.UnusedFormalParameter")
  public AgathonSeedProvider(Map<String, String> unused) {
    this(new AgathonConnector());
  }

  @VisibleForTesting AgathonSeedProvider(AgathonConnector connector) {
    LOG.info("Using AgathonSeedProvider!");
    this.connector = connector;
  }

  @Override
  public List<InetAddress> getSeeds() {
    try {
      return transform(connector.getSeeds());
    } catch (ConfigurationException e) {
      throw Throwables.propagate(e);
    }
  }

  private List<InetAddress> transform(List<String> seeds) {
    ImmutableList.Builder<InetAddress> seedBuilder = ImmutableList.builder();
    for (String seed : seeds) {
      InetAddress address = getInetAddress(seed);
      if (address != null) {
        seedBuilder.add(address);
      }
    }
    return seedBuilder.build();
  }

  @VisibleForTesting @Nullable InetAddress getInetAddress(String host) {
    try {
      return InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Unknown Cassandra host: " + host, e);
      }
    }
    return null;
  }

}

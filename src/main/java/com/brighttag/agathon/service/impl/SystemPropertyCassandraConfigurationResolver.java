package com.brighttag.agathon.service.impl;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;

import org.apache.cassandra.locator.Ec2MultiRegionSnitch;
import org.apache.cassandra.locator.IEndpointSnitch;

import com.brighttag.agathon.cassandra.AgathonSeedProvider;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.SnitchConfiguration;

/**
 * Reads in a Cassandra configuration from system properties.
 *
 * @author codyaray
 * @since 8/3/12
 */
class SystemPropertyCassandraConfigurationResolver implements CassandraConfigurationResolver {

  @VisibleForTesting static final String CLUSTER_NAME = "com.brighttag.agathon.config.cluster_name";
  @VisibleForTesting static final String ENDPOINT_SNITCH = "com.brighttag.agathon.config.endpoint_snitch";

  @Override
  public CassandraConfiguration getConfiguration(CassandraInstance unused,
      CassandraConfiguration chainedConfiguration) {
    return new CassandraConfiguration.Builder(chainedConfiguration)
        .clusterName(Objects.firstNonNull(
            System.getProperty(CLUSTER_NAME), chainedConfiguration.getClusterName()))
        .seedProvider(AgathonSeedProvider.class)
        .snitchConfiguration(new SnitchConfiguration.Builder(chainedConfiguration.getSnitchConfiguration())
            .endpointSnitch(this.<IEndpointSnitch>optClass(
                System.getProperty(ENDPOINT_SNITCH), Ec2MultiRegionSnitch.class))
            .build())
        .build();
  }

  @SuppressWarnings("unchecked")
  private <V> Class<? extends V> optClass(@Nullable String className, Class<? extends V> defaultClass) {
    try {
      return className == null ? defaultClass : (Class<? extends V>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      return defaultClass;
    }
  }

}

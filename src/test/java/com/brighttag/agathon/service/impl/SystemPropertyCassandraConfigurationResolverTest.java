package com.brighttag.agathon.service.impl;

import org.apache.cassandra.locator.Ec2MultiRegionSnitch;
import org.apache.cassandra.locator.Ec2Snitch;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.cassandra.AgathonSeedProvider;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationResolver;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 8/3/12
 */
public class SystemPropertyCassandraConfigurationResolverTest {

  private CassandraConfigurationResolver resolver;

  @Before
  public void setUp() {
    resolver = new SystemPropertyCassandraConfigurationResolver();
  }

  @Test
  public void getConfiguration_noOverrides() {
    CassandraConfiguration expected = new CassandraConfiguration.Builder()
        .seedProvider(AgathonSeedProvider.class)
        .snitchConfiguration(new SnitchConfiguration.Builder()
            .endpointSnitch(Ec2MultiRegionSnitch.class)
            .build())
        .build();
    assertEquals(expected, resolver.getConfiguration(CassandraConfiguration.DEFAULT));
  }

  @Test
  public void getConfiguration_allAvailableOverrides() {
    System.setProperty(SystemPropertyCassandraConfigurationResolver.CLUSTER_NAME, "Cluster Corn");
    System.setProperty(SystemPropertyCassandraConfigurationResolver.ENDPOINT_SNITCH,
        "org.apache.cassandra.locator.Ec2Snitch");
    CassandraConfiguration chainedConfiguration = CassandraConfiguration.DEFAULT;
    CassandraConfiguration expected = new CassandraConfiguration.Builder()
        .clusterName("Cluster Corn")
        .seedProvider(AgathonSeedProvider.class)
        .snitchConfiguration(new SnitchConfiguration.Builder()
            .endpointSnitch(Ec2Snitch.class)
            .build())
        .build();
    assertEquals(expected, resolver.getConfiguration(chainedConfiguration));
    System.clearProperty(SystemPropertyCassandraConfigurationResolver.CLUSTER_NAME);
    System.clearProperty(SystemPropertyCassandraConfigurationResolver.ENDPOINT_SNITCH);
  }

}

package com.brighttag.agathon.resources.yaml.config;

import org.apache.cassandra.locator.Ec2MultiRegionSnitch;
import org.joda.time.Period;
import org.junit.Test;

import com.brighttag.agathon.model.config.SnitchConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class SnitchConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    SnitchConfiguration config = createConfig(MIN_CASSANDRA_YAML, new SnitchConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the SnitchConfigurationTest.
     */
    assertEquals(SnitchConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    SnitchConfiguration config = createConfig(MAX_CASSANDRA_YAML, new SnitchConfigurationReader());
    assertEquals(Ec2MultiRegionSnitch.class, config.getEndpointSnitch());
    assertTrue(config.getDynamicSnitch().get());
    assertEquals(Period.millis(100), config.getDynamicSnitchUpdateInterval().get());
    assertEquals(Period.minutes(10), config.getDynamicSnitchResetInterval().get());
    assertEquals(0.0, config.getDynamicSnitchBadnessThreshold().get(), 0.01);
  }

}

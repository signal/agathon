package com.brighttag.agathon.resources.yaml.config;

import org.joda.time.Duration;
import org.junit.Test;

import com.brighttag.agathon.model.config.HintedHandoffConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class HintedHandoffConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    HintedHandoffConfiguration config = createConfig(MIN_CASSANDRA_YAML,
        new HintedHandoffConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the HintedHandoffConfigurationTest.
     */
    assertEquals(HintedHandoffConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    HintedHandoffConfiguration config = createConfig(MAX_CASSANDRA_YAML,
        new HintedHandoffConfigurationReader());
    assertTrue(config.getEnabled().get());
    assertEquals(Duration.standardHours(1), config.getMaxHintWindow().get());
    assertEquals(new Duration(50), config.getThrottleDelay().get());
  }

}

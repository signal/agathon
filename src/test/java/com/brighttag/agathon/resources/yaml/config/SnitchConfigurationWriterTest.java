package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class SnitchConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getSnitchConfiguration(), new SnitchConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "endpoint_snitch: org.apache.cassandra.locator.SimpleSnitch",
        ""
    ), config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getSnitchConfiguration(), new SnitchConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "endpoint_snitch: org.apache.cassandra.locator.Ec2MultiRegionSnitch",
        "dynamic_snitch: true",
        "dynamic_snitch_update_interval_in_ms: 100",
        "dynamic_snitch_reset_interval_in_ms: 600000",
        "dynamic_snitch_badness_threshold: 0.0",
        ""
    ), config);
  }

}

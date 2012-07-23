package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class HintedHandoffConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getHintedHandoffConfiguration(),
        new HintedHandoffConfigurationWriter());
    assertEquals("{}\n", config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getHintedHandoffConfiguration(),
        new HintedHandoffConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "hinted_handoff_enabled: true",
        "max_hint_window_in_ms: 3600000",
        "hinted_handoff_throttle_delay_in_ms: 50",
        ""
    ), config);
  }

}

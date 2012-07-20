package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.brighttag.agathon.model.config.HintedHandoffConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link HintedHandoffConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class HintedHandoffConfigurationReader
    extends AbstractConfigurationReader<HintedHandoffConfiguration> {

  @Override
  public HintedHandoffConfiguration fromYaml(YamlObject config) throws YamlException {
    return new HintedHandoffConfiguration.Builder()
        .enabled(config.optBoolean("hinted_handoff_enabled", null))
        .maxHintWindow(optDuration(config, "max_hint_window_in_ms"))
        .throttleDelay(optDuration(config, "hinted_handoff_throttle_delay_in_ms"))
        .build();
  }

}

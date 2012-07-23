package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.brighttag.agathon.model.config.HintedHandoffConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link HintedHandoffConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class HintedHandoffConfigurationWriter
    extends AbstractConfigurationWriter<HintedHandoffConfiguration> {

  @Override
  public YamlObject toYaml(HintedHandoffConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .putIfNotNull("hinted_handoff_enabled", config.getEnabled())
        .putIfNotNull("max_hint_window_in_ms", optDuration(config.getMaxHintWindow()))
        .putIfNotNull("hinted_handoff_throttle_delay_in_ms", optDuration(config.getThrottleDelay()))
        .build();
  }

}

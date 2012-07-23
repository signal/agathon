package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link SnitchConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class SnitchConfigurationWriter
    extends AbstractConfigurationWriter<SnitchConfiguration> {

  @Override
  public YamlObject toYaml(SnitchConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .put("endpoint_snitch", config.getEndpointSnitch().getName())
        .putIfNotNull("dynamic_snitch", config.getDynamicSnitch())
        .putIfNotNull("dynamic_snitch_update_interval_in_ms",
            optPeriod(config.getDynamicSnitchUpdateInterval()))
        .putIfNotNull("dynamic_snitch_reset_interval_in_ms",
            optPeriod(config.getDynamicSnitchResetInterval()))
        .putIfNotNull("dynamic_snitch_badness_threshold",
            config.getDynamicSnitchBadnessThreshold())
        .build();
  }

}

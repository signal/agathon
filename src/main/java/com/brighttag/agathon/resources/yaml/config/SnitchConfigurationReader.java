package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import org.apache.cassandra.locator.IEndpointSnitch;

import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link SnitchConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class SnitchConfigurationReader
    extends AbstractConfigurationReader<SnitchConfiguration> {

  @Override
  public SnitchConfiguration fromYaml(YamlObject config) throws YamlException {
    return new SnitchConfiguration.Builder()
        .endpointSnitch(this.<IEndpointSnitch>optClass(config, "endpoint_snitch"))
        .dynamicSnitch(config.optBoolean("dynamic_snitch", null))
        .dynamicSnitchUpdateInterval(optPeriod(config, "dynamic_snitch_update_interval_in_ms"))
        .dynamicSnitchResetInterval(optPeriod(config, "dynamic_snitch_reset_interval_in_ms"))
        .dynamicSnitchBadnessThreshold(config.optDouble("dynamic_snitch_badness_threshold", null))
        .build();
  }

}

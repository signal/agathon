package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link CommitLogConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CommitLogConfigurationWriter
    extends AbstractConfigurationWriter<CommitLogConfiguration> {

  @Override
  public YamlObject toYaml(CommitLogConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .put("commitlog_directory", config.getDirectory().getAbsolutePath())
        .put("commitlog_sync", config.getSyncMode().toString().toLowerCase())
        .putIfNotNull("commitlog_sync_period_in_ms", optPeriod(config.getSyncPeriod()))
        .putIfNotNull("commitlog_sync_batch_window_in_ms", optDuration(config.getSyncBatchWindow()))
        .putIfNotNull("commitlog_rotation_threshold_in_mb",
            optDataSize(config.getRotation(), DataSize.Unit.MEGABYTE))
        .build();
  }

}

package com.brighttag.agathon.resources.yaml.config;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.model.config.CommitLogConfiguration.SyncMode;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link CommitLogConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CommitLogConfigurationReader
    extends AbstractConfigurationReader<CommitLogConfiguration> {

  @Override
  public CommitLogConfiguration fromYaml(YamlObject config) throws YamlException {
    return new CommitLogConfiguration.Builder()
        .directory(optFile(config, "commitlog_directory"))
        .syncMode(buildSyncMode(config, "commitlog_sync"))
        .syncPeriod(optPeriod(config, "commitlog_sync_period_in_ms"))
        .syncBatchWindow(optDuration(config, "commitlog_sync_batch_window_in_ms"))
        .rotation(optDataSize(config, "commitlog_rotation_threshold_in_mb", DataSize.Unit.MEGABYTE))
        .build();
  }

  private @Nullable SyncMode buildSyncMode(YamlObject config, String key) throws YamlException {
    return config.has(key) ? SyncMode.valueOf(config.getString(key).toUpperCase()) : null;
  }

}

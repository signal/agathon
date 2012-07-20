package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.CompactionConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link CompactionConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CompactionConfigurationReader
    extends AbstractConfigurationReader<CompactionConfiguration> {

  @Override
  public CompactionConfiguration fromYaml(YamlObject config) throws YamlException {
    return new CompactionConfiguration.Builder()
        .snapshotBeforeCompaction(config.optBoolean("snapshot_before_compaction", null))
        .threadPriority(config.optInt("compaction_thread_priority", null))
        .inMemoryLimit(optDataSize(config, "in_memory_compaction_limit_in_mb", DataSize.Unit.MEGABYTE))
        .concurrentCompactors(config.optInt("concurrent_compactors", null))
        .throughputMbPerSec(config.optInt("compaction_throughput_mb_per_sec", null))
        .preheatKeyCache(config.optBoolean("compaction_preheat_key_cache", null))
        .build();
  }

}

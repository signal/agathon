package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.CompactionConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link CompactionConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CompactionConfigurationWriter
    extends AbstractConfigurationWriter<CompactionConfiguration> {

  @Override
  public YamlObject toYaml(CompactionConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .putIfNotNull("snapshot_before_compaction", config.getSnapshotBeforeCompaction())
        .putIfNotNull("compaction_thread_priority", config.getThreadPriority())
        .putIfNotNull("in_memory_compaction_limit_in_mb",
            optDataSize(config.getInMemoryLimit(), DataSize.Unit.MEGABYTE))
        .putIfNotNull("concurrent_compactors", config.getConcurrentCompactors())
        .putIfNotNull("compaction_throughput_mb_per_sec", config.getThroughputMbPerSec())
        .putIfNotNull("compaction_preheat_key_cache", config.getPreheatKeyCache())
        .build();
  }

}

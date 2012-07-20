package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.PerformanceConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link PerformanceConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class PerformanceConfigurationReader
    extends AbstractConfigurationReader<PerformanceConfiguration> {

  @Override
  public PerformanceConfiguration fromYaml(YamlObject config) throws YamlException {
    return new PerformanceConfiguration.Builder()
        .flushLargestMemtablesAtHeapUsageFraction(config.optDouble("flush_largest_memtables_at", null))
        .reduceCacheSizesAtHeapUsageFraction(config.optDouble("reduce_cache_sizes_at", null))
        .reduceCacheCapacityToCurrentSizeFraction(config.optDouble("reduce_cache_capacity_to", null))
        .concurrentReads(config.optInt("concurrent_reads", null))
        .concurrentWrites(config.optInt("concurrent_writes", null))
        .memtableTotalSpace(optDataSize(config, "memtable_total_space_in_mb", DataSize.Unit.MEGABYTE))
        .memtableFlushWriters(config.optInt("memtable_flush_writers", null))
        .memtableFlushQueueSize(config.optInt("memtable_flush_queue_size", null))
        .slicedBufferSize(optDataSize(config, "sliced_buffer_size_in_kb", DataSize.Unit.KILOBYTE))
        .thriftFramedTransportSize(optDataSize(config,
            "thrift_framed_transport_size_in_mb", DataSize.Unit.MEGABYTE))
        .thriftMaxMessageSize(optDataSize(config,
            "thrift_max_message_length_in_mb", DataSize.Unit.MEGABYTE))
        .columnIndexSize(optDataSize(config, "column_index_size_in_kb", DataSize.Unit.KILOBYTE))
        .phiConvictThreshold(config.optInt("phi_convict_threshold", null))
        .build();
  }

}

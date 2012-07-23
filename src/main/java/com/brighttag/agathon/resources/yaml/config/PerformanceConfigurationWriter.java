package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.PerformanceConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link PerformanceConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class PerformanceConfigurationWriter
    extends AbstractConfigurationWriter<PerformanceConfiguration> {

  @Override
  public YamlObject toYaml(PerformanceConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .putIfNotNull("flush_largest_memtables_at", config.getFlushLargestMemtablesAtHeapUsageFraction())
        .putIfNotNull("reduce_cache_sizes_at", config.getReduceCacheSizesAtHeapUsageFraction())
        .putIfNotNull("reduce_cache_capacity_to", config.getReduceCacheCapacityToCurrentSizeFraction())
        .putIfNotNull("concurrent_reads", config.getConcurrentReads())
        .putIfNotNull("concurrent_writes", config.getConcurrentWrites())
        .putIfNotNull("memtable_total_space_in_mb",
            optDataSize(config.getMemtableTotalSpace(), DataSize.Unit.MEGABYTE))
        .putIfNotNull("memtable_flush_writers", config.getMemtableFlushWriters())
        .putIfNotNull("memtable_flush_queue_size", config.getMemtableFlushQueueSize())
        .putIfNotNull("sliced_buffer_size_in_kb",
            optDataSize(config.getSlicedBufferSize(), DataSize.Unit.KILOBYTE))
        .putIfNotNull("thrift_framed_transport_size_in_mb",
            optDataSize(config.getThriftFramedTransportSize(), DataSize.Unit.MEGABYTE))
        .putIfNotNull("thrift_max_message_length_in_mb",
            optDataSize(config.getThriftMaxMessageSize(), DataSize.Unit.MEGABYTE))
        .putIfNotNull("column_index_size_in_kb",
            optDataSize(config.getColumnIndexSize(), DataSize.Unit.KILOBYTE))
        .putIfNotNull("phi_convict_threshold", config.getPhiConvictThreshold())
        .build();
  }

}

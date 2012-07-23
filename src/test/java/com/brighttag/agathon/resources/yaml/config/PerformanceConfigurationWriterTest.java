package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class PerformanceConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getPerformanceConfiguration(),
        new PerformanceConfigurationWriter());
    assertEquals("{}\n", config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getPerformanceConfiguration(),
        new PerformanceConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "flush_largest_memtables_at: 0.75",
        "reduce_cache_sizes_at: 0.85",
        "reduce_cache_capacity_to: 0.6",
        "concurrent_reads: 32",
        "concurrent_writes: 32",
        "memtable_total_space_in_mb: 2048",
        "memtable_flush_writers: 1",
        "memtable_flush_queue_size: 4",
        "sliced_buffer_size_in_kb: 64",
        "thrift_framed_transport_size_in_mb: 15",
        "thrift_max_message_length_in_mb: 16",
        "column_index_size_in_kb: 64",
        "phi_convict_threshold: 8",
        ""
    ), config);
  }

}

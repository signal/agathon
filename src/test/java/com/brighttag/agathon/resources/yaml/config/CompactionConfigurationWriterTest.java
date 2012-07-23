package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class CompactionConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getCompactionConfiguration(),
        new CompactionConfigurationWriter());
    assertEquals("{}\n", config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getCompactionConfiguration(),
        new CompactionConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "snapshot_before_compaction: false",
        "compaction_thread_priority: 1",
        "in_memory_compaction_limit_in_mb: 64",
        "concurrent_compactors: 1",
        "compaction_throughput_mb_per_sec: 16",
        "compaction_preheat_key_cache: true",
        ""
    ), config);
  }

}

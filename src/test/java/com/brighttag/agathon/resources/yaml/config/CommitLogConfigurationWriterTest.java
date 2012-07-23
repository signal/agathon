package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class CommitLogConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getCommitLogConfiguration(), new CommitLogConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "commitlog_directory: /var/lib/cassandra/commitlog",
        "commitlog_sync: periodic",
        "commitlog_sync_period_in_ms: 10000",
        ""
    ), config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getCommitLogConfiguration(), new CommitLogConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "commitlog_directory: /mnt/lib/cassandra/commitlog",
        "commitlog_sync: batch",
        "commitlog_sync_period_in_ms: 15000",
        "commitlog_sync_batch_window_in_ms: 50",
        "commitlog_rotation_threshold_in_mb: 128",
        ""
    ), config);
  }

}

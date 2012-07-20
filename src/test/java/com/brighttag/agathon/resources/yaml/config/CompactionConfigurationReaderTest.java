package com.brighttag.agathon.resources.yaml.config;

import com.proofpoint.units.DataSize;

import org.junit.Test;

import com.brighttag.agathon.model.config.CompactionConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class CompactionConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    CompactionConfiguration config = createConfig(MIN_CASSANDRA_YAML, new CompactionConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the CompactionConfigurationTest.
     */
    assertEquals(CompactionConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    CompactionConfiguration config = createConfig(MAX_CASSANDRA_YAML, new CompactionConfigurationReader());
    assertFalse(config.getSnapshotBeforeCompaction().get());
    assertEquals(1, config.getThreadPriority().get().intValue());
    assertEquals(new DataSize(64, DataSize.Unit.MEGABYTE), config.getInMemoryLimit().get());
    assertEquals(1, config.getConcurrentCompactors().get().intValue());
    assertEquals(16, config.getThroughputMbPerSec().get().intValue());
    assertTrue(config.getPreheatKeyCache().get());
  }

}

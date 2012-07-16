package com.brighttag.agathon.model.config;

import com.google.common.testing.EqualsTester;
import com.proofpoint.units.DataSize;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class CompactionConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().snapshotBeforeCompaction(true).build())
      .addEqualityGroup(builder().threadPriority(5).build())
      .addEqualityGroup(builder().inMemoryLimit(DataSize.valueOf("1.2 GB")).build())
      .addEqualityGroup(builder().concurrentCompactors(4).build())
      .addEqualityGroup(builder().throughputMbPerSec(25).build())
      .addEqualityGroup(builder().preheatKeyCache(true).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    CompactionConfiguration config = CompactionConfiguration.DEFAULT;
    assertFalse(config.getSnapshotBeforeCompaction().isPresent());
    assertFalse(config.getThreadPriority().isPresent());
    assertFalse(config.getInMemoryLimit().isPresent());
    assertFalse(config.getConcurrentCompactors().isPresent());
    assertFalse(config.getThroughputMbPerSec().isPresent());
    assertFalse(config.getPreheatKeyCache().isPresent());
  }

  private CompactionConfiguration.Builder builder() {
    return new CompactionConfiguration.Builder();
  }
}

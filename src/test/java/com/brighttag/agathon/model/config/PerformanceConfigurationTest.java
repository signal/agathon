package com.brighttag.agathon.model.config;

import com.google.common.testing.EqualsTester;
import com.proofpoint.units.DataSize;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class PerformanceConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().flushLargestMemtablesAtHeapUsageFraction(0.75).build())
      .addEqualityGroup(builder().reduceCacheSizesAtHeapUsageFraction(0.85).build())
      .addEqualityGroup(builder().reduceCacheCapacityToCurrentSizeFraction(0.6).build())
      .addEqualityGroup(builder().concurrentReads(32).build())
      .addEqualityGroup(builder().concurrentWrites(32).build())
      .addEqualityGroup(builder().memtableTotalSpace(DataSize.valueOf("2048MB")).build())
      .addEqualityGroup(builder().memtableFlushWriters(1).build())
      .addEqualityGroup(builder().memtableFlushQueueSize(4).build())
      .addEqualityGroup(builder().slicedBufferSize(DataSize.valueOf("64kB")).build())
      .addEqualityGroup(builder().thriftFramedTransportSize(DataSize.valueOf("15MB")).build())
      .addEqualityGroup(builder().thriftMaxMessageSize(DataSize.valueOf("16MB")).build())
      .addEqualityGroup(builder().columnIndexSize(DataSize.valueOf("64kB")).build())
      .addEqualityGroup(builder().phiConvictThreshold(8).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    PerformanceConfiguration config = PerformanceConfiguration.DEFAULT;
    assertFalse(config.getFlushLargestMemtablesAtHeapUsageFraction().isPresent());
    assertFalse(config.getReduceCacheSizesAtHeapUsageFraction().isPresent());
    assertFalse(config.getReduceCacheCapacityToCurrentSizeFraction().isPresent());
    assertFalse(config.getConcurrentReads().isPresent());
    assertFalse(config.getConcurrentWrites().isPresent());
    assertFalse(config.getMemtableTotalSpace().isPresent());
    assertFalse(config.getMemtableFlushWriters().isPresent());
    assertFalse(config.getMemtableFlushQueueSize().isPresent());
    assertFalse(config.getSlicedBufferSize().isPresent());
    assertFalse(config.getThriftFramedTransportSize().isPresent());
    assertFalse(config.getThriftMaxMessageSize().isPresent());
    assertFalse(config.getColumnIndexSize().isPresent());
    assertFalse(config.getPhiConvictThreshold().isPresent());
  }

  private PerformanceConfiguration.Builder builder() {
    return new PerformanceConfiguration.Builder();
  }
}

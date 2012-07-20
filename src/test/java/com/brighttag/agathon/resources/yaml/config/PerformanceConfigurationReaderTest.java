package com.brighttag.agathon.resources.yaml.config;

import com.proofpoint.units.DataSize;

import org.junit.Test;

import com.brighttag.agathon.model.config.PerformanceConfiguration;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class PerformanceConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    PerformanceConfiguration config = createConfig(MIN_CASSANDRA_YAML, new PerformanceConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the PerformanceConfigurationTest.
     */
    assertEquals(PerformanceConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    PerformanceConfiguration config = createConfig(MAX_CASSANDRA_YAML, new PerformanceConfigurationReader());
    assertEquals(0.75, config.getFlushLargestMemtablesAtHeapUsageFraction().get().doubleValue(), 0.01);
    assertEquals(0.85, config.getReduceCacheSizesAtHeapUsageFraction().get().doubleValue(), 0.01);
    assertEquals(0.6, config.getReduceCacheCapacityToCurrentSizeFraction().get().doubleValue(), 0.01);
    assertEquals(32, config.getConcurrentReads().get().doubleValue(), 0.01);
    assertEquals(32, config.getConcurrentWrites().get().doubleValue(), 0.01);
    assertEquals(new DataSize(2048, DataSize.Unit.MEGABYTE), config.getMemtableTotalSpace().get());
    assertEquals(1, config.getMemtableFlushWriters().get().doubleValue(), 0.01);
    assertEquals(4, config.getMemtableFlushQueueSize().get().doubleValue(), 0.01);
    assertEquals(new DataSize(64, DataSize.Unit.KILOBYTE), config.getSlicedBufferSize().get());
    assertEquals(new DataSize(15, DataSize.Unit.MEGABYTE), config.getThriftFramedTransportSize().get());
    assertEquals(new DataSize(16, DataSize.Unit.MEGABYTE), config.getThriftMaxMessageSize().get());
    assertEquals(new DataSize(64, DataSize.Unit.KILOBYTE), config.getColumnIndexSize().get());
    assertEquals(8, config.getPhiConvictThreshold().get().intValue());
  }

}

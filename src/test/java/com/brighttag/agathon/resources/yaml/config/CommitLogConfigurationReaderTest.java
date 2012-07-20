package com.brighttag.agathon.resources.yaml.config;

import java.io.File;

import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Test;

import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.model.config.CommitLogConfiguration.SyncMode;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class CommitLogConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    CommitLogConfiguration config = createConfig(MIN_CASSANDRA_YAML, new CommitLogConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the CommitLogConfigurationTest.
     */
    assertEquals(CommitLogConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    CommitLogConfiguration config = createConfig(MAX_CASSANDRA_YAML, new CommitLogConfigurationReader());
    assertEquals(new File("/mnt/lib/cassandra/commitlog"), config.getDirectory());
    assertEquals(SyncMode.BATCH, config.getSyncMode());
    assertEquals(Period.seconds(15), config.getSyncPeriod().get());
    assertEquals(new Duration(50), config.getSyncBatchWindow().get());
    assertEquals(new DataSize(128, DataSize.Unit.MEGABYTE), config.getRotation().get());
  }

}

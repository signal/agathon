package com.brighttag.agathon.model.config;

import java.io.File;

import com.google.common.testing.EqualsTester;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Test;

import com.brighttag.agathon.model.config.CommitLogConfiguration.SyncMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class CommitLogConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().directory(new File("/")).build())
      .addEqualityGroup(builder().syncMode(SyncMode.BATCH).build())
      .addEqualityGroup(builder().syncBatchWindow(Duration.standardSeconds(2)).build())
      .addEqualityGroup(builder().syncPeriod(Period.seconds(30)).build())
      .addEqualityGroup(builder().rotation(DataSize.valueOf("30MB")).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    CommitLogConfiguration config = CommitLogConfiguration.DEFAULT;
    assertEquals(new File("/var/lib/cassandra/commitlog"), config.getDirectory());
    assertEquals(SyncMode.PERIODIC, config.getSyncMode());
    assertEquals(Period.seconds(10), config.getSyncPeriod().get());
    assertFalse(config.getSyncBatchWindow().isPresent());
    assertFalse(config.getRotation().isPresent());
  }

  private CommitLogConfiguration.Builder builder() {
    return new CommitLogConfiguration.Builder();
  }
}

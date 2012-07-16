package com.brighttag.agathon.model.config;

import java.io.File;
import java.math.BigInteger;
import java.text.ParseException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostSpecifier;
import com.google.common.testing.EqualsTester;

import org.apache.cassandra.auth.AllowAllAuthenticator;
import org.apache.cassandra.auth.AllowAllAuthority;
import org.apache.cassandra.dht.ByteOrderedPartitioner;
import org.apache.cassandra.dht.RandomPartitioner;
import org.apache.cassandra.locator.SimpleSeedProvider;
import org.apache.cassandra.scheduler.NoScheduler;
import org.junit.Test;

import com.brighttag.agathon.cassandra.AgathonSeedProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class CassandraConfigurationTest {

  @Test
  public void equals() throws ParseException {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().clusterName("cluster-corn").build())
      .addEqualityGroup(builder().partitioner(ByteOrderedPartitioner.class).build())
      .addEqualityGroup(builder().seedProvider(AgathonSeedProvider.class).build())
      .addEqualityGroup(builder().seedProviderOptions(ImmutableMap.of("key", "val")).build())
      .addEqualityGroup(builder().dataFileDirectories(ImmutableSet.of(new File("/data"))).build())
      .addEqualityGroup(builder().savedCachesDirectory(new File("/caches")).build())
      .addEqualityGroup(builder().initialToken(BigInteger.TEN).build())
      .addEqualityGroup(builder().autoBootstrap(true).build())
      .addEqualityGroup(builder().authenticator(AllowAllAuthenticator.class).build())
      .addEqualityGroup(builder().authority(AllowAllAuthority.class).build())
      .addEqualityGroup(builder().storagePort(90210).build())
      .addEqualityGroup(builder().listenAddress(HostSpecifier.from("127.0.0.2")).build())
      .addEqualityGroup(builder().incrementalBackups(true).build())
      .addEqualityGroup(builder().requestScheduler(NoScheduler.class).build())
      .addEqualityGroup(builder().requestSchedulerOptions(
          new RequestSchedulerOptions.Builder().throttleLimit(80).build()).build())
      .addEqualityGroup(builder().commitLogConfiguration(
          new CommitLogConfiguration.Builder().directory(new File("/logs")).build()).build())
      .addEqualityGroup(builder().compactionConfiguration(
          new CompactionConfiguration.Builder().concurrentCompactors(4).build()).build())
      .addEqualityGroup(builder().hintedHandoffConfiguration(
          new HintedHandoffConfiguration.Builder().enabled(true).build()).build())
      .addEqualityGroup(builder().performanceConfiguration(
          new PerformanceConfiguration.Builder().concurrentReads(32).build()).build())
      .addEqualityGroup(builder().rpcConfiguration(
          new RpcConfiguration.Builder().keepalive(true).build()).build())
      .testEquals();
  }

  @Test(expected = NullPointerException.class)
  public void build_nullClusterName() {
    builder().clusterName(null).build();
  }

  @Test(expected = NullPointerException.class)
  public void build_nullSeedProviderOptions() {
    builder().seedProviderOptions(null).build();
  }

  @Test(expected = NullPointerException.class)
  public void build_nullDataFileDirectories() {
    builder().dataFileDirectories(null).build();
  }

  @Test
  public void accessors_requiredAttributes() {
    CassandraConfiguration config = builder().build();
    assertEquals("cluster", config.getClusterName());
  }

  @Test
  public void accessors_optionalAttributes_defaults() {
    CassandraConfiguration config = builder().build();
    assertEquals(RandomPartitioner.class, config.getPartitioner());
    assertEquals(SimpleSeedProvider.class, config.getSeedProvider());
    assertEquals(ImmutableMap.of("seeds", "127.0.0.1"), config.getSeedProviderOptions());
    assertEquals(ImmutableSet.of(new File("/var/lib/cassandra/data")), config.getDataFileDirectories());
    assertEquals(new File("/var/lib/cassandra/saved_caches"), config.getSavedCachesDirectory());
    assertFalse(config.getInitialToken().isPresent());
    assertFalse(config.getAutoBootstrap().get());
    assertFalse(config.getAuthenticator().isPresent());
    assertFalse(config.getAuthority().isPresent());
    assertFalse(config.getStoragePort().isPresent());
    assertFalse(config.getListenAddress().isPresent());
    assertFalse(config.getIncrementalBackups().isPresent());
    assertFalse(config.getRequestScheduler().isPresent());
    assertFalse(config.getRequestSchedulerOptions().isPresent());
    assertEquals(HintedHandoffConfiguration.DEFAULT, config.getHintedHandoffConfiguration());
    assertEquals(CommitLogConfiguration.DEFAULT, config.getCommitLogConfiguration());
    assertEquals(PerformanceConfiguration.DEFAULT, config.getPerformanceConfiguration());
    assertEquals(RpcConfiguration.DEFAULT, config.getRpcConfiguration());
    assertEquals(CompactionConfiguration.DEFAULT, config.getCompactionConfiguration());
    assertEquals(SnitchConfiguration.DEFAULT, config.getSnitchConfiguration());
  }

  @Test
  public void default_emptySeedProviderOptions() {
    assertEquals(ImmutableMap.of("seeds", "127.0.0.1"),
        builder().seedProviderOptions(ImmutableMap.<String, String>of()).build().getSeedProviderOptions());
  }

  @Test
  public void default_emptyDataFileDirectories() {
    assertEquals(ImmutableSet.of(new File("/var/lib/cassandra/data")),
        builder().dataFileDirectories(ImmutableSet.<File>of()).build().getDataFileDirectories());
  }

  private CassandraConfiguration.Builder builder() {
    return new CassandraConfiguration.Builder().clusterName("cluster");
  }
}

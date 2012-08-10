package com.brighttag.agathon.resources.yaml.config;

import java.io.File;
import java.math.BigInteger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostSpecifier;

import org.apache.cassandra.auth.AllowAllAuthenticator;
import org.apache.cassandra.auth.AllowAllAuthority;
import org.apache.cassandra.dht.ByteOrderedPartitioner;
import org.apache.cassandra.scheduler.RoundRobinScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.cassandra.AgathonSeedProvider;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.model.config.CompactionConfiguration;
import com.brighttag.agathon.model.config.HintedHandoffConfiguration;
import com.brighttag.agathon.model.config.PerformanceConfiguration;
import com.brighttag.agathon.model.config.RequestSchedulerOptions;
import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.yaml.YamlObject;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 7/13/12
 */
public class CassandraConfigurationReaderTest extends AbstractConfigurationReaderTest {

  private static final HintedHandoffConfiguration HINTED_HANDOFF_CONFIG = HintedHandoffConfiguration.DEFAULT;
  private static final CommitLogConfiguration COMMIT_LOG_CONFIG = CommitLogConfiguration.DEFAULT;
  private static final PerformanceConfiguration PERFORMANCE_CONFIG = PerformanceConfiguration.DEFAULT;
  private static final RpcConfiguration RPC_CONFIG = RpcConfiguration.DEFAULT;
  private static final CompactionConfiguration COMPACTION_CONFIG = CompactionConfiguration.DEFAULT;
  private static final SnitchConfiguration SNITCH_CONFIG = SnitchConfiguration.DEFAULT;

  private HintedHandoffConfigurationReader hintedHandoffConfigurationReader;
  private CommitLogConfigurationReader commitLogConfigurationReader;
  private PerformanceConfigurationReader performanceConfigurationReader;
  private RpcConfigurationReader rpcConfigurationReader;
  private CompactionConfigurationReader compactionConfigurationReader;
  private SnitchConfigurationReader snitchConfigurationReader;
  private CassandraConfigurationReader reader;

  @Before
  public void setUp() throws Exception {
    hintedHandoffConfigurationReader = createMock(HintedHandoffConfigurationReader.class);
    commitLogConfigurationReader = createMock(CommitLogConfigurationReader.class);
    performanceConfigurationReader = createMock(PerformanceConfigurationReader.class);
    rpcConfigurationReader = createMock(RpcConfigurationReader.class);
    compactionConfigurationReader = createMock(CompactionConfigurationReader.class);
    snitchConfigurationReader = createMock(SnitchConfigurationReader.class);
    expect(hintedHandoffConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(HINTED_HANDOFF_CONFIG);
    expect(commitLogConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(COMMIT_LOG_CONFIG);
    expect(performanceConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(PERFORMANCE_CONFIG);
    expect(rpcConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(RPC_CONFIG);
    expect(compactionConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(COMPACTION_CONFIG);
    expect(snitchConfigurationReader.fromYaml(isA(YamlObject.class))).andReturn(SNITCH_CONFIG);
    replayAll();
    reader = new CassandraConfigurationReader(
          hintedHandoffConfigurationReader, commitLogConfigurationReader,
          performanceConfigurationReader, rpcConfigurationReader,
          compactionConfigurationReader, snitchConfigurationReader);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getConfiguration_min() throws Exception {
    CassandraConfiguration config = createConfig(MIN_CASSANDRA_YAML, reader);
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the CassandraConfigurationTest.
     */
    assertEquals(CassandraConfiguration.DEFAULT, config);
  }

  @Test
  public void getConfiguration_max() throws Exception {
    CassandraConfiguration config = createConfig(MAX_CASSANDRA_YAML, reader);
    assertEquals("My Cluster", config.getClusterName());
    assertEquals(ByteOrderedPartitioner.class, config.getPartitioner());
    assertEquals(AgathonSeedProvider.class, config.getSeedProvider());
    assertEquals(ImmutableMap.of("seeds", "127.0.0.2"), config.getSeedProviderOptions());
    assertEquals(ImmutableSet.of(new File("/mnt/lib/cassandra/data")), config.getDataFileDirectories());
    assertEquals(new File("/mnt/lib/cassandra/saved_caches"), config.getSavedCachesDirectory());
    assertEquals(BigInteger.TEN, config.getInitialToken().get());
    assertTrue(config.getAutoBootstrap().get());
    assertEquals(AllowAllAuthenticator.class, config.getAuthenticator().get());
    assertEquals(AllowAllAuthority.class, config.getAuthority().get());
    assertEquals(7000, config.getStoragePort().get().intValue());
    assertEquals(HostSpecifier.fromValid("127.0.0.1"), config.getListenAddress().get());
    assertFalse(config.getIncrementalBackups().get());
    assertEquals(RoundRobinScheduler.class, config.getRequestScheduler().get());
    assertCustomRequestSchedulerOptions(config.getRequestSchedulerOptions().get());
    assertEquals(HINTED_HANDOFF_CONFIG, config.getHintedHandoffConfiguration());
    assertEquals(COMMIT_LOG_CONFIG, config.getCommitLogConfiguration());
    assertEquals(PERFORMANCE_CONFIG, config.getPerformanceConfiguration());
    assertEquals(RPC_CONFIG, config.getRpcConfiguration());
    assertEquals(COMPACTION_CONFIG, config.getCompactionConfiguration());
    assertEquals(SNITCH_CONFIG, config.getSnitchConfiguration());

  }

  private void assertCustomRequestSchedulerOptions(RequestSchedulerOptions config) {
    assertEquals(80, config.getThrottleLimit());
    assertEquals(2, config.getDefaultWeight().get().intValue());
    assertEquals(ImmutableMap.of("Keyspace1", 1, "Keyspace2", 5), config.getWeights());
  }

}

package com.brighttag.agathon.resources.yaml.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.model.config.CompactionConfiguration;
import com.brighttag.agathon.model.config.HintedHandoffConfiguration;
import com.brighttag.agathon.model.config.PerformanceConfiguration;
import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.yaml.YamlObject;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/13/12
 */
public class CassandraConfigurationWriterTest extends AbstractConfigurationWriterTest {

  private static final YamlObject HINTED_HANDOFF_YAML = newYamlObject("hintedhandoff", "yaml");
  private static final YamlObject COMMIT_LOG_YAML = newYamlObject("commitlog", "yaml");
  private static final YamlObject PERFORMANCE_YAML = newYamlObject("performance", "yaml");
  private static final YamlObject RPC_YAML = newYamlObject("rpc", "yaml");
  private static final YamlObject COMPACTION_YAML = newYamlObject("compaction", "yaml");
  private static final YamlObject SNITCH_YAML = newYamlObject("snitch", "yaml");

  private HintedHandoffConfigurationWriter hintedHandoffConfigWriter;
  private CommitLogConfigurationWriter commitLogConfigWriter;
  private PerformanceConfigurationWriter performanceConfigWriter;
  private RpcConfigurationWriter rpcConfigWriter;
  private CompactionConfigurationWriter compactionConfigWriter;
  private SnitchConfigurationWriter snitchConfigWriter;
  private CassandraConfigurationWriter writer;

  @Before
  public void setUp() throws Exception {
    hintedHandoffConfigWriter = createMock(HintedHandoffConfigurationWriter.class);
    commitLogConfigWriter = createMock(CommitLogConfigurationWriter.class);
    performanceConfigWriter = createMock(PerformanceConfigurationWriter.class);
    rpcConfigWriter = createMock(RpcConfigurationWriter.class);
    compactionConfigWriter = createMock(CompactionConfigurationWriter.class);
    snitchConfigWriter = createMock(SnitchConfigurationWriter.class);
    writer = new CassandraConfigurationWriter(
          hintedHandoffConfigWriter, commitLogConfigWriter,
          performanceConfigWriter, rpcConfigWriter,
          compactionConfigWriter, snitchConfigWriter);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void toYaml_min() throws Exception {
    expectSubConfigurations(new YamlObject(), new YamlObject(), new YamlObject(),
        new YamlObject(), new YamlObject(), new YamlObject());
    replayAll();
    String config = createConfig(MIN_CONFIG, writer);
    assertEquals(LINE_JOINER.join(
        "cluster_name: cluster",
        "partitioner: org.apache.cassandra.dht.RandomPartitioner",
        "seed_provider:",
        "  class_name: org.apache.cassandra.locator.SimpleSeedProvider",
        "  parameters:",
        "    seeds: 127.0.0.1",
        "data_file_directories:",
        "- /var/lib/cassandra/data",
        "saved_caches_directory: /var/lib/cassandra/saved_caches",
        "auto_bootstrap: false",
        ""
    ), config);
  }

  @Test
  public void toYaml_max() throws Exception {
    expectSubConfigurations(HINTED_HANDOFF_YAML, COMMIT_LOG_YAML, PERFORMANCE_YAML,
        RPC_YAML, COMPACTION_YAML, SNITCH_YAML);
    replayAll();
    String config = createConfig(MAX_CONFIG, writer);
    assertEquals(LINE_JOINER.join(
        "cluster_name: Test Cluster",
        "partitioner: org.apache.cassandra.dht.ByteOrderedPartitioner",
        "seed_provider:",
        "  class_name: com.brighttag.agathon.cassandra.AgathonSeedProvider",
        "  parameters:",
        "    seeds: 127.0.0.2",
        "data_file_directories:",
        "- /mnt/lib/cassandra/data",
        "saved_caches_directory: /mnt/lib/cassandra/saved_caches",
        "initial_token: 10",
        "auto_bootstrap: true",
        "authenticator: org.apache.cassandra.auth.AllowAllAuthenticator",
        "authority: org.apache.cassandra.auth.AllowAllAuthority",
        "storage_port: 7000",
        "listen_address: 127.0.0.1",
        "incremental_backups: false",
        "request_scheduler: org.apache.cassandra.scheduler.RoundRobinScheduler",
        "request_scheduler_options:",
        "  throttle_limit: 80",
        "  default_weight: 2",
        "  weights:",
        "    Keyspace1: 1",
        "    Keyspace2: 5",
        "hintedhandoff: yaml",
        "commitlog: yaml",
        "performance: yaml",
        "rpc: yaml",
        "compaction: yaml",
        "snitch: yaml",
        ""
    ), config);
  }

  private static YamlObject newYamlObject(String key, String value) {
    return new YamlObject.Builder().put(key, value).build();
  }

  private void expectSubConfigurations(
      YamlObject handoffYaml, YamlObject commitlogYaml, YamlObject performanceYaml,
      YamlObject rpcYaml, YamlObject compactionYaml, YamlObject snitchYaml) throws Exception {
    expect(hintedHandoffConfigWriter.toYaml(isA(HintedHandoffConfiguration.class))).andReturn(handoffYaml);
    expect(commitLogConfigWriter.toYaml(isA(CommitLogConfiguration.class))).andReturn(commitlogYaml);
    expect(performanceConfigWriter.toYaml(isA(PerformanceConfiguration.class))).andReturn(performanceYaml);
    expect(rpcConfigWriter.toYaml(isA(RpcConfiguration.class))).andReturn(rpcYaml);
    expect(compactionConfigWriter.toYaml(isA(CompactionConfiguration.class))).andReturn(compactionYaml);
    expect(snitchConfigWriter.toYaml(isA(SnitchConfiguration.class))).andReturn(snitchYaml);
  }

}

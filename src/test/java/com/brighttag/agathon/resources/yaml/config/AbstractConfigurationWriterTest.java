package com.brighttag.agathon.resources.yaml.config;

import java.io.File;
import java.math.BigInteger;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.common.net.HostSpecifier;
import com.proofpoint.units.DataSize;

import org.apache.cassandra.auth.AllowAllAuthenticator;
import org.apache.cassandra.auth.AllowAllAuthority;
import org.apache.cassandra.dht.ByteOrderedPartitioner;
import org.apache.cassandra.locator.Ec2MultiRegionSnitch;
import org.apache.cassandra.scheduler.RoundRobinScheduler;
import org.easymock.EasyMockSupport;
import org.joda.time.Duration;
import org.joda.time.Period;

import com.brighttag.agathon.cassandra.AgathonSeedProvider;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.CommitLogConfiguration;
import com.brighttag.agathon.model.config.CommitLogConfiguration.SyncMode;
import com.brighttag.agathon.model.config.CompactionConfiguration;
import com.brighttag.agathon.model.config.HintedHandoffConfiguration;
import com.brighttag.agathon.model.config.PerformanceConfiguration;
import com.brighttag.agathon.model.config.RequestSchedulerOptions;
import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.model.config.RpcConfiguration.ServerType;
import com.brighttag.agathon.model.config.SnitchConfiguration;
import com.brighttag.agathon.resources.yaml.YamlWriter;

/**
 * Base class and fixtures used for testing the Yaml configuration writers.
 *
 * @author codyaray
 * @since 7/22/12
 */
public abstract class AbstractConfigurationWriterTest extends EasyMockSupport {

  protected static final Joiner LINE_JOINER = Joiner.on("\n");

  protected <T> String createConfig(T obj, YamlWriter<T> writer) throws Exception {
    return writer.toYaml(obj).toString();
  }

  protected static final CassandraConfiguration MIN_CONFIG = new CassandraConfiguration.Builder()
      .clusterName("cluster")
      .build();

  protected static final CassandraConfiguration MAX_CONFIG = new CassandraConfiguration.Builder()
    .clusterName("Test Cluster")
    .initialToken(BigInteger.TEN)
    .autoBootstrap(true)
    .hintedHandoffConfiguration(new HintedHandoffConfiguration.Builder()
        .enabled(true)
        .maxHintWindow(Duration.standardHours(1))
        .throttleDelay(new Duration(50))
        .build())
    .authenticator(AllowAllAuthenticator.class)
    .authority(AllowAllAuthority.class)
    .partitioner(ByteOrderedPartitioner.class)
    .dataFileDirectories(ImmutableSet.of(new File("/mnt/lib/cassandra/data")))
    .savedCachesDirectory(new File("/mnt/lib/cassandra/saved_caches"))
    .commitLogConfiguration(new CommitLogConfiguration.Builder()
        .directory(new File("/mnt/lib/cassandra/commitlog"))
        .rotation(DataSize.valueOf("128MB"))
        .syncMode(SyncMode.BATCH)
        .syncPeriod(Period.seconds(15))
        .syncBatchWindow(new Duration(50))
        .build())
    .seedProvider(AgathonSeedProvider.class)
    .seedProviderOptions(ImmutableMap.of("seeds", "127.0.0.2"))
    .performanceConfiguration(new PerformanceConfiguration.Builder()
        .flushLargestMemtablesAtHeapUsageFraction(0.75)
        .reduceCacheSizesAtHeapUsageFraction(0.85)
        .reduceCacheCapacityToCurrentSizeFraction(0.6)
        .concurrentReads(32)
        .concurrentWrites(32)
        .memtableTotalSpace(DataSize.valueOf("2048MB"))
        .memtableFlushWriters(1)
        .memtableFlushQueueSize(4)
        .slicedBufferSize(DataSize.valueOf("64kB"))
        .columnIndexSize(DataSize.valueOf("64kB"))
        .thriftFramedTransportSize(DataSize.valueOf("15MB"))
        .thriftMaxMessageSize(DataSize.valueOf("16MB"))
        .phiConvictThreshold(8)
        .build())
    .storagePort(7000)
    .listenAddress(HostSpecifier.fromValid("127.0.0.1"))
    .rpcConfiguration(new RpcConfiguration.Builder()
        .hostAndPort(HostAndPort.fromParts("localhost", 9160))
        .keepalive(true)
        .serverType(ServerType.SYNC)
        .minThreads(16)
        .maxThreads(2048)
        .sendBuffSize(DataSize.valueOf("2048B"))
        .recvBuffSize(DataSize.valueOf("2048B"))
        .timeout(Duration.standardSeconds(10))
        .build())
    .compactionConfiguration(new CompactionConfiguration.Builder()
        .snapshotBeforeCompaction(false)
        .threadPriority(1)
        .inMemoryLimit(DataSize.valueOf("64MB"))
        .concurrentCompactors(1)
        .throughputMbPerSec(16)
        .preheatKeyCache(true)
        .build())
    .incrementalBackups(false)
    .snitchConfiguration(new SnitchConfiguration.Builder()
        .endpointSnitch(Ec2MultiRegionSnitch.class)
        .dynamicSnitch(true)
        .dynamicSnitchUpdateInterval(Period.millis(100))
        .dynamicSnitchResetInterval(Period.seconds(600))
        .dynamicSnitchBadnessThreshold(0.0)
        .build())
    .requestScheduler(RoundRobinScheduler.class)
    .requestSchedulerOptions(new RequestSchedulerOptions.Builder()
        .throttleLimit(80)
        .defaultWeight(2)
        .weights(ImmutableMap.of("Keyspace1", 1, "Keyspace2", 5))
        .build())
    .build();
//    .request_scheduler_id(keyspace)
//    .index_interval(128)
//    "encryption_options:",
//    .  internode_encryption(none)
//    .  keystore(conf/.keystore)
//    .  keystore_password(cassandra)
//    .  truststore(conf/.truststore)
//    "  truststore_password: cassandra");

}

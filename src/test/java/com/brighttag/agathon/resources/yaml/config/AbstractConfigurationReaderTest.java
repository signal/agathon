package com.brighttag.agathon.resources.yaml.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.google.common.base.Joiner;

import org.easymock.EasyMockSupport;

import com.brighttag.agathon.resources.yaml.YamlReader;
import com.brighttag.yaml.YamlObject;

/**
 * Base class and fixtures used for testing the Yaml configuration readers.
 *
 * @author codyaray
 * @since 7/16/12
 */
public abstract class AbstractConfigurationReaderTest extends EasyMockSupport {

  protected <T> T createConfig(String yaml, YamlReader<T> reader) throws Exception {
    InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());
    return reader.fromYaml(new YamlObject(inputStream));
  }

  protected static final String MIN_CASSANDRA_YAML = Joiner.on("\n").join(
    "# Cassandra storage config YAML",
    "cluster_name: 'Test Cluster'");

  protected static final String MAX_CASSANDRA_YAML = Joiner.on("\n").join(
    "# Cassandra storage config YAML",
    "cluster_name: 'Test Cluster'",
    "initial_token: 10",
    "auto_bootstrap: true",
    "hinted_handoff_enabled: true",
    "max_hint_window_in_ms: 3600000 # one hour",
    "hinted_handoff_throttle_delay_in_ms: 50",
    "authenticator: org.apache.cassandra.auth.AllowAllAuthenticator",
    "authority: org.apache.cassandra.auth.AllowAllAuthority",
    "partitioner: org.apache.cassandra.dht.ByteOrderedPartitioner",
    "data_file_directories:",
    "    - /mnt/lib/cassandra/data",
    "commitlog_directory: /mnt/lib/cassandra/commitlog",
    "saved_caches_directory: /mnt/lib/cassandra/saved_caches",
    "commitlog_rotation_threshold_in_mb: 128",
    "commitlog_sync: batch",
    "commitlog_sync_period_in_ms: 15000",
    "commitlog_sync_batch_window_in_ms: 50",
    "seed_provider:",
    "  - class_name: com.brighttag.agathon.cassandra.AgathonSeedProvider",
    "    parameters:",
    "        # seeds is actually a comma-delimited list of addresses.",
    "        # Ex: \"<ip1>,<ip2>,<ip3>\"",
    "        - seeds: \"127.0.0.2\"",
    "flush_largest_memtables_at: 0.75",
    "reduce_cache_sizes_at: 0.85",
    "reduce_cache_capacity_to: 0.6",
    "concurrent_reads: 32",
    "concurrent_writes: 32",
    "memtable_total_space_in_mb: 2048",
    "memtable_flush_writers: 1",
    "memtable_flush_queue_size: 4",
    "sliced_buffer_size_in_kb: 64",
    "storage_port: 7000",
    "listen_address: 127.0.0.1",
    "rpc_address: localhost",
    "rpc_port: 9160",
    "rpc_keepalive: true",
    "rpc_server_type: sync",
    "rpc_min_threads: 16",
    "rpc_max_threads: 2048",
    "rpc_send_buff_size_in_bytes: 2048",
    "rpc_recv_buff_size_in_bytes: 2048",
    "thrift_framed_transport_size_in_mb: 15",
    "thrift_max_message_length_in_mb: 16",
    "incremental_backups: false",
    "snapshot_before_compaction: false",
    "compaction_thread_priority: 1",
    "column_index_size_in_kb: 64",
    "in_memory_compaction_limit_in_mb: 64",
    "concurrent_compactors: 1",
    "compaction_throughput_mb_per_sec: 16",
    "compaction_preheat_key_cache: true",
    "rpc_timeout_in_ms: 10000",
    "phi_convict_threshold: 8",
    "endpoint_snitch: org.apache.cassandra.locator.Ec2MultiRegionSnitch",
    "dynamic_snitch: true",
    "dynamic_snitch_update_interval_in_ms: 100",
    "dynamic_snitch_reset_interval_in_ms: 600000",
    "dynamic_snitch_badness_threshold: 0.0",
    "request_scheduler: org.apache.cassandra.scheduler.RoundRobinScheduler",
    "request_scheduler_options:",
    "  throttle_limit: 80",
    "  default_weight: 2",
    "  weights:",
    "    Keyspace1: 1",
    "    Keyspace2: 5",
    "request_scheduler_id: keyspace",
    "index_interval: 128",
    "encryption_options:",
    "  internode_encryption: none",
    "  keystore: conf/.keystore",
    "  keystore_password: cassandra",
    "  truststore: conf/.truststore",
    "  truststore_password: cassandra");

}

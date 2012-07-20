package com.brighttag.agathon.resources.yaml.config;

import java.io.File;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostSpecifier;
import com.google.inject.Inject;

import org.apache.cassandra.auth.IAuthenticator;
import org.apache.cassandra.auth.IAuthority;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.locator.SeedProvider;
import org.apache.cassandra.scheduler.IRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.RequestSchedulerOptions;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlArray;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link CassandraConfiguration}.
 *
 * @author codyaray
 * @since 7/13/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CassandraConfigurationReader
    extends AbstractConfigurationReader<CassandraConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(CassandraConfigurationReader.class);

  private final HintedHandoffConfigurationReader hintedHandoffConfigReader;
  private final CommitLogConfigurationReader commitLogConfigReader;
  private final PerformanceConfigurationReader performanceConfigReader;
  private final RpcConfigurationReader rpcConfigReader;
  private final CompactionConfigurationReader compactionConfigReader;
  private final SnitchConfigurationReader snitchConfigReader;

  @Inject
  public CassandraConfigurationReader(
      HintedHandoffConfigurationReader hintedHandoffConfigReader,
      CommitLogConfigurationReader commitLogConfigReader,
      PerformanceConfigurationReader performanceConfigReader,
      RpcConfigurationReader rpcConfigReader,
      CompactionConfigurationReader compactionConfigReader,
      SnitchConfigurationReader snitchConfigReader) {
    this.hintedHandoffConfigReader = hintedHandoffConfigReader;
    this.commitLogConfigReader = commitLogConfigReader;
    this.performanceConfigReader = performanceConfigReader;
    this.rpcConfigReader = rpcConfigReader;
    this.compactionConfigReader = compactionConfigReader;
    this.snitchConfigReader = snitchConfigReader;
  }

  @Override
  public @Nullable CassandraConfiguration fromYaml(YamlObject config) throws YamlException {
    YamlObject seedProvider = config.has("seed_provider") ?
        config.getArray("seed_provider").getMap(0) : new YamlObject();
    return new CassandraConfiguration.Builder()
        .clusterName(config.getString("cluster_name"))
        .partitioner(this.<IPartitioner<?>>optClass(config, "partitioner"))
        .seedProvider(this.<SeedProvider>optClass(seedProvider, "class_name"))
        .seedProviderOptions(optSeedProviderOptions(seedProvider))
        .dataFileDirectories(optDataDirs(config.optArray("data_file_directories")))
        .savedCachesDirectory(optFile(config, "saved_caches_directory"))
        .initialToken(optToken(config, "initial_token"))
        .autoBootstrap(config.optBoolean("auto_bootstrap", null))
        .authenticator(this.<IAuthenticator>optClass(config, "authenticator"))
        .authority(this.<IAuthority>optClass(config, "authority"))
        .storagePort(config.optInt("storage_port", null))
        .listenAddress(buildHostSpecifier(config, "listen_address"))
        .incrementalBackups(config.optBoolean("incremental_backups", null))
        .requestScheduler(this.<IRequestScheduler>optClass(config, "request_scheduler"))
        .requestSchedulerOptions(buildRequestSchedulerOptions(
            config.optMap("request_scheduler_options")))
        .hintedHandoffConfiguration(hintedHandoffConfigReader.fromYaml(config))
        .commitLogConfiguration(commitLogConfigReader.fromYaml(config))
        .performanceConfiguration(performanceConfigReader.fromYaml(config))
        .rpcConfiguration(rpcConfigReader.fromYaml(config))
        .compactionConfiguration(compactionConfigReader.fromYaml(config))
        .snitchConfiguration(snitchConfigReader.fromYaml(config))
        .build();
  }

  private @Nullable RequestSchedulerOptions buildRequestSchedulerOptions(@Nullable YamlObject config)
    throws YamlException {
    if (config == null) {
      return null;
    }
    ImmutableMap.Builder<String, Integer> weightsBuilder = ImmutableMap.builder();
    if (config.has("weights")) {
      for (Map.Entry<?, ?> entry : config.getMap("weights").asMap().entrySet()) {
        weightsBuilder.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
      }
    }
    return new RequestSchedulerOptions.Builder()
        .throttleLimit(config.optInt("throttle_limit", null))
        .defaultWeight(config.optInt("default_weight", null))
        .weights(weightsBuilder.build())
        .build();
  }

  private @Nullable HostSpecifier buildHostSpecifier(YamlObject config, String key) throws YamlException {
    if (!config.has(key)) {
      return null;
    }
    HostSpecifier host = null;
    try {
      host = HostSpecifier.from(config.getString(key));
    } catch (ParseException e) {
      logException(key, config.getString(key), e);
    }
    return host;
  }

  private @Nullable BigInteger optToken(YamlObject config, String key) throws YamlException {
    return config.has(key) ? new BigInteger(config.getString(key)) : null;
  }

  private Map<String, String> optSeedProviderOptions(YamlObject seedProvider) throws YamlException {
    YamlArray parameters = seedProvider.optArray("parameters");
    if (parameters == null || parameters.size() == 0) {
      return ImmutableMap.of();
    }

    ImmutableMap.Builder<String, String> optionsBuilder = ImmutableMap.builder();
    for (Map.Entry<?, ?> entry : parameters.getMap(0).asMap().entrySet()) {
      optionsBuilder.put(entry.getKey().toString(), entry.getValue().toString());
    }
    return optionsBuilder.build();
  }

  private Set<File> optDataDirs(@Nullable YamlArray array) throws YamlException {
    if (array == null) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<File> directories = ImmutableSet.builder();
    for (int i = 0; i < array.size(); i++) {
      directories.add(new File(array.getString(i)));
    }
    return directories.build();
  }

  private static void logException(String key, String value, Exception e) {
    LOG.warn("Invalid cassandra configuration for {}: {}", key, value);
  }

}

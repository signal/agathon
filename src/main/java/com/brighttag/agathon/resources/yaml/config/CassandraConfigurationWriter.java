package com.brighttag.agathon.resources.yaml.config;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import org.apache.cassandra.locator.SeedProvider;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.model.config.RequestSchedulerOptions;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlArray;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link CassandraConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/13/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class CassandraConfigurationWriter
    extends AbstractConfigurationWriter<CassandraConfiguration> {

  private final HintedHandoffConfigurationWriter hintedHandoffConfigWriter;
  private final CommitLogConfigurationWriter commitLogConfigWriter;
  private final PerformanceConfigurationWriter performanceConfigWriter;
  private final RpcConfigurationWriter rpcConfigWriter;
  private final CompactionConfigurationWriter compactionConfigWriter;
  private final SnitchConfigurationWriter snitchConfigWriter;

  @Inject
  public CassandraConfigurationWriter(
      HintedHandoffConfigurationWriter hintedHandoffConfigWriter,
      CommitLogConfigurationWriter commitLogConfigWriter,
      PerformanceConfigurationWriter performanceConfigWriter,
      RpcConfigurationWriter rpcConfigWriter,
      CompactionConfigurationWriter compactionConfigWriter,
      SnitchConfigurationWriter snitchConfigWriter) {
    this.hintedHandoffConfigWriter = hintedHandoffConfigWriter;
    this.commitLogConfigWriter = commitLogConfigWriter;
    this.performanceConfigWriter = performanceConfigWriter;
    this.rpcConfigWriter = rpcConfigWriter;
    this.compactionConfigWriter = compactionConfigWriter;
    this.snitchConfigWriter = snitchConfigWriter;
  }

  @Override
  public YamlObject toYaml(CassandraConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .put("cluster_name", config.getClusterName())
        .put("partitioner", config.getPartitioner().getName())
        .put("seed_provider", optSeedProvider(config.getSeedProvider(), config.getSeedProviderOptions()))
        .put("data_file_directories", optDataDirs(config.getDataFileDirectories()))
        .put("saved_caches_directory", config.getSavedCachesDirectory().getAbsolutePath())
        .putIfNotNull("initial_token", config.getInitialToken())
        .putIfNotNull("auto_bootstrap", config.getAutoBootstrap())
        .putIfNotNull("authenticator", config.getAuthenticator().isPresent() ?
            config.getAuthenticator().get().getName() : null)
        .putIfNotNull("authority", config.getAuthority().isPresent() ?
            config.getAuthority().get().getName() : null)
        .putIfNotNull("storage_port", config.getStoragePort())
        .putIfNotNull("listen_address", config.getListenAddress().isPresent() ?
            config.getListenAddress().get().toString() : null)
        .putIfNotNull("incremental_backups", config.getIncrementalBackups())
        .putIfNotNull("request_scheduler", config.getRequestScheduler().isPresent() ?
            config.getRequestScheduler().get().getName() : null)
        .putIfNotNull("request_scheduler_options", buildRequestSchedulerOptions(
            config.getRequestSchedulerOptions()))
        .putIfNotNull(hintedHandoffConfigWriter.toYaml(config.getHintedHandoffConfiguration()))
        .putIfNotNull(commitLogConfigWriter.toYaml(config.getCommitLogConfiguration()))
        .putIfNotNull(performanceConfigWriter.toYaml(config.getPerformanceConfiguration()))
        .putIfNotNull(rpcConfigWriter.toYaml(config.getRpcConfiguration()))
        .putIfNotNull(compactionConfigWriter.toYaml(config.getCompactionConfiguration()))
        .putIfNotNull(snitchConfigWriter.toYaml(config.getSnitchConfiguration()))
        .build();
  }

  private YamlObject optSeedProvider(Class<? extends SeedProvider> seedProvider,
      Map<String, String> options) {
    YamlObject.Builder parameters = new YamlObject.Builder();
    for (Map.Entry<String, String> parameter : options.entrySet()) {
      parameters.put(parameter.getKey(), parameter.getValue());
    }
    return new YamlObject.Builder()
        .put("class_name", seedProvider.getName())
        .put("parameters", parameters.build())
        .build();
  }

  private @Nullable YamlObject buildRequestSchedulerOptions(
      Optional<RequestSchedulerOptions> options) throws YamlException {
    if (! options.isPresent()) {
      return null;
    }
    YamlObject.Builder weights = new YamlObject.Builder();
    for (Map.Entry<String, Integer> weight : options.get().getWeights().entrySet()) {
      weights.put(weight.getKey(), weight.getValue());
    }
    return new YamlObject.Builder()
        .put("throttle_limit", options.get().getThrottleLimit())
        .put("default_weight", options.get().getDefaultWeight().get())
        .put("weights", weights.build())
        .build();
  }

  private YamlArray optDataDirs(Set<File> dataDirs) throws YamlException {
    YamlArray.Builder directories = new YamlArray.Builder();
    for (File dataDir : dataDirs) {
      directories.add(dataDir.getAbsolutePath());
    }
    return directories.build();
  }

}

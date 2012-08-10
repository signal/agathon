package com.brighttag.agathon.model.config;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostSpecifier;

import org.apache.cassandra.auth.IAuthenticator;
import org.apache.cassandra.auth.IAuthority;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.dht.RandomPartitioner;
import org.apache.cassandra.locator.SeedProvider;
import org.apache.cassandra.locator.SimpleSeedProvider;
import org.apache.cassandra.scheduler.IRequestScheduler;

/**
 * An immutable Cassandra configuration containing all the configuration parameters
 * from {@code cassandra.yaml}. Most parameters are {@link Optional}, allowing Cassandra
 * to use its own default values. In a few rare (but important) cases, a required parameter
 * is given a default matching the Cassandra documentation and out-of-the-box configuration file.
 *
 * Example usage:<pre>{@code
 *     return new CassandraConfiguration.Builder()
 *         .clusterName("My Cluster")
 *         .initialToken(BigInteger.ZERO)
 *         .seedProvider(AgathonSeedProvider.class)
 *         .snitchConfiguration(new SnitchConfiguration.Builder()
 *             .endpointSnitch(Ec2MultiRegionSnitch.class)
 *             .build())
 *         .build();
 * }</pre>
 *
 * @author codyaray
 * @since 6/26/12
 */
public class CassandraConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final CassandraConfiguration DEFAULT = new Builder().build();

  /**
   * The name of this cluster. This is mainly used to prevent machines
   * in one logical cluster from joining another. Defaults to "Test Cluster".
   */
  private final String clusterName;

  /**
   * Backend used to distribute rows (by key) across nodes in the cluster.
   * Defaults to {@link RandomPartitioner}.
   */
  private final Class<? extends IPartitioner<?>> partitioner;

  /**
   * Backend used to provide the seed nodes for the cluster.
   * Defaults to {@link SimpleSeedProvider}.
   */
  private final Class<? extends SeedProvider> seedProvider;

  /**
   * Seed provider options which vary based on the type of provider.
   * Defaults to {@code { "seeds" => "127.0.0.1" }}
   */
  private final ImmutableMap<String, String> seedProviderOptions;

  /**
   * Directories where Cassandra should store data on disk. Defaults to "/var/lib/cassandra/data".
   */
  private final ImmutableSet<File> dataFileDirectories;

  /**
   * Directory where Cassandra should store its caches. Defaults to "/var/lib/cassandra/saved_caches".
   */
  private final File savedCachesDirectory;

  /**
   * You should always specify initialToken when setting up a production
   * cluster for the first time, and often when adding capacity later.
   * The principle is that each node should be given an equal slice of
   * the token ring.
   *
   * If {@link Optional#absent() absent}, Cassandra will request a token
   * bisecting the range of the heaviest-loaded existing node. If there is
   * no load information available, such as is the case with a new cluster,
   * it will pick a random token, which will lead to hot spots.
   *
   * @see <a href="http://wiki.apache.org/cassandra/Operations">Cassandra Operations</a>
   */
  private final Optional<BigInteger> initialToken;

  /**
   * Set to true to make new [non-seed] nodes automatically migrate data
   * to themselves from the pre-existing nodes in the cluster. Defaults
   * to false because you can only bootstrap N machines at a time from
   * an existing cluster of N, so if you are bringing up a cluster of
   * 10 machines with 3 seeds you would have to do it in stages. Leaving
   * this off for the initial start simplifies that.
   */
  private final Optional<Boolean> autoBootstrap;

  /**
   * Authentication backend used to identify users.
   */
  private final Optional<Class<? extends IAuthenticator>> authenticator;

  /**
   * Authorization backend used to limit access/provide permissions.
   */
  private final Optional<Class<? extends IAuthority>> authority;

  /**
   * TCP port used for commands and data.
   */
  private final Optional<Integer> storagePort;

  /**
   * Address to bind to and tell other Cassandra nodes to connect to. You
   * <i>must</i> change this if you want multiple nodes to be able to
   * communicate!
   *
   * Leaving it blank leaves it up to InetAddress.getLocalHost(). This
   * will always do the Right Thing <b>if</b> the node is properly configured
   * (hostname, name resolution, etc), and the Right Thing is to use the
   * address associated with the hostname (it might not be).
   *
   * Setting this to 0.0.0.0 is always wrong.
   */
  private final Optional<HostSpecifier> listenAddress; // better datatype here?

  /**
   * Set to true to have Cassandra create a hard link to each sstable
   * flushed or streamed locally in a backups/ subdirectory of the
   * Keyspace data.  Removing these links is the operator's
   * responsibility.
   */
  private final Optional<Boolean> incrementalBackups;

  /**
   * Schedule incoming client requests according to the specific policy.
   * This is useful for multi-tenancy with a single Cassandra cluster.
   *
   * NOTE: This is specifically for requests from the client and does
   * not affect inter node communication.
   */
  private final Optional<Class<? extends IRequestScheduler>> requestScheduler;

  /**
   * Scheduler options vary based on the type of scheduler.
   */
  private final Optional<RequestSchedulerOptions> requestSchedulerOptions;

  /**
   * Configuration options related to hinted handoff.
   */
  private final HintedHandoffConfiguration hintedHandoffConfiguration;

  /**
   * Configuration options related to the commit log.
   */
  private final CommitLogConfiguration commitLogConfiguration;

  /**
   * Configuration options related to Cassandra's performance.
   */
  private final PerformanceConfiguration performanceConfiguration;

  /**
   * Configuration options related to Cassandra's RPC server.
   */
  private final RpcConfiguration rpcConfiguration;

  /**
   * Configuration options related to compaction.
   */
  private final CompactionConfiguration compactionConfiguration;

  /**
   * Configuration options related to the endpoint snitch.
   */
  private final SnitchConfiguration snitchConfiguration;

  private CassandraConfiguration(Builder builder) {
    this.clusterName = builder.clusterName;
    this.partitioner = builder.partitioner;
    this.seedProvider = builder.seedProvider;
    this.seedProviderOptions = builder.seedProviderOptions;
    this.dataFileDirectories = builder.dataFileDirectories;
    this.savedCachesDirectory = builder.savedCachesDirectory;
    this.initialToken = builder.initialToken;
    this.autoBootstrap = builder.autoBootstrap;
    this.authenticator = builder.authenticator;
    this.authority = builder.authority;
    this.storagePort = builder.storagePort;
    this.listenAddress = builder.listenAddress;
    this.incrementalBackups = builder.incrementalBackups;
    this.requestScheduler = builder.requestScheduler;
    this.requestSchedulerOptions = builder.requestSchedulerOptions;
    this.hintedHandoffConfiguration = builder.hintedHandoffConfiguration;
    this.commitLogConfiguration = builder.commitLogConfiguration;
    this.performanceConfiguration = builder.performanceConfiguration;
    this.rpcConfiguration = builder.rpcConfiguration;
    this.compactionConfiguration = builder.compactionConfiguration;
    this.snitchConfiguration = builder.snitchConfiguration;
  }

  public String getClusterName() {
    return clusterName;
  }

  public Class<? extends IPartitioner<?>> getPartitioner() {
    return partitioner;
  }

  public Class<? extends SeedProvider> getSeedProvider() {
    return seedProvider;
  }

  public Map<String, String> getSeedProviderOptions() {
    return seedProviderOptions;
  }

  public ImmutableSet<File> getDataFileDirectories() {
    return dataFileDirectories;
  }

  public File getSavedCachesDirectory() {
    return savedCachesDirectory;
  }

  public Optional<BigInteger> getInitialToken() {
    return initialToken;
  }

  public Optional<Boolean> getAutoBootstrap() {
    return autoBootstrap;
  }

  public Optional<Class<? extends IAuthenticator>> getAuthenticator() {
    return authenticator;
  }

  public Optional<Class<? extends IAuthority>> getAuthority() {
    return authority;
  }

  public Optional<Integer> getStoragePort() {
    return storagePort;
  }

  public Optional<HostSpecifier> getListenAddress() {
    return listenAddress;
  }

  public Optional<Boolean> getIncrementalBackups() {
    return incrementalBackups;
  }

  public Optional<Class<? extends IRequestScheduler>> getRequestScheduler() {
    return requestScheduler;
  }

  public Optional<RequestSchedulerOptions> getRequestSchedulerOptions() {
    return requestSchedulerOptions;
  }

  public HintedHandoffConfiguration getHintedHandoffConfiguration() {
    return hintedHandoffConfiguration;
  }

  public CommitLogConfiguration getCommitLogConfiguration() {
    return commitLogConfiguration;
  }

  public PerformanceConfiguration getPerformanceConfiguration() {
    return performanceConfiguration;
  }

  public RpcConfiguration getRpcConfiguration() {
    return rpcConfiguration;
  }

  public CompactionConfiguration getCompactionConfiguration() {
    return compactionConfiguration;
  }

  public SnitchConfiguration getSnitchConfiguration() {
    return snitchConfiguration;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(significantAttributes());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj == this) {
      return true;
    } else if (!getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    return Arrays.equals(significantAttributes(), getClass().cast(obj).significantAttributes());
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("clusterName", clusterName)
        .add("partitioner", partitioner)
        .add("seedProvider", seedProvider)
        .add("seedProviderOptions", seedProviderOptions)
        .add("dataFileDirectories", dataFileDirectories)
        .add("savedCachesDirectory", savedCachesDirectory)
        .add("initialToken", initialToken)
        .add("autoBootstrap", autoBootstrap)
        .add("authenticator", authenticator)
        .add("authority", authority)
        .add("storagePort", storagePort)
        .add("listenAddress", listenAddress)
        .add("incrementalBackups", incrementalBackups)
        .add("requestScheduler", requestScheduler)
        .add("requestSchedulerOptions", requestSchedulerOptions)
        .add("hintedHandoffConfiguration", hintedHandoffConfiguration)
        .add("commitLogConfiguration", commitLogConfiguration)
        .add("performanceConfiguration", performanceConfiguration)
        .add("rpcConfiguration", rpcConfiguration)
        .add("compactionConfiguration", compactionConfiguration)
        .add("snitchConfiguration", snitchConfiguration)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {
      clusterName, partitioner, seedProvider, seedProviderOptions, dataFileDirectories,
      savedCachesDirectory, initialToken, autoBootstrap, authenticator, authority,
      storagePort, listenAddress, incrementalBackups, requestScheduler, requestSchedulerOptions,
      hintedHandoffConfiguration, commitLogConfiguration, performanceConfiguration,
      rpcConfiguration, compactionConfiguration, snitchConfiguration,
    };
  }

  /**
   * Fluent builder for {@link CassandraConfiguration}s.
   *
   * @author codyaray
   * @since 6/26/12
   */
  public static class Builder {

    /*
     * Required attributes with defaults set in build()
     */
    private @Nullable String clusterName;
    private @Nullable Class<? extends IPartitioner<?>> partitioner;
    private @Nullable Class<? extends SeedProvider> seedProvider;
    private @Nullable ImmutableMap<String, String> seedProviderOptions;
    private @Nullable ImmutableSet<File> dataFileDirectories;
    private @Nullable File savedCachesDirectory;

    /*
     * Optional attributes
     */
    private Optional<BigInteger> initialToken = Optional.absent();
    private Optional<Boolean> autoBootstrap = Optional.absent();
    private Optional<Class<? extends IAuthenticator>> authenticator = Optional.absent();
    private Optional<Class<? extends IAuthority>> authority = Optional.absent();
    private Optional<Integer> storagePort = Optional.absent();
    private Optional<HostSpecifier> listenAddress = Optional.absent();
    private Optional<Boolean> incrementalBackups = Optional.absent();
    private Optional<Class<? extends IRequestScheduler>> requestScheduler = Optional.absent();
    private Optional<RequestSchedulerOptions> requestSchedulerOptions = Optional.absent();

    private @Nullable HintedHandoffConfiguration hintedHandoffConfiguration;
    private @Nullable CommitLogConfiguration commitLogConfiguration;
    private @Nullable PerformanceConfiguration performanceConfiguration;
    private @Nullable RpcConfiguration rpcConfiguration;
    private @Nullable CompactionConfiguration compactionConfiguration;
    private @Nullable SnitchConfiguration snitchConfiguration;

    public Builder() {
      // Nothing to do
    }

    public Builder(CassandraConfiguration configuration) {
      this.clusterName = configuration.clusterName;
      this.partitioner = configuration.partitioner;
      this.seedProvider = configuration.seedProvider;
      this.seedProviderOptions = configuration.seedProviderOptions;
      this.dataFileDirectories = configuration.dataFileDirectories;
      this.savedCachesDirectory = configuration.savedCachesDirectory;
      this.initialToken = configuration.initialToken;
      this.autoBootstrap = configuration.autoBootstrap;
      this.authenticator = configuration.authenticator;
      this.authority = configuration.authority;
      this.storagePort = configuration.storagePort;
      this.listenAddress = configuration.listenAddress;
      this.incrementalBackups = configuration.incrementalBackups;
      this.requestScheduler = configuration.requestScheduler;
      this.requestSchedulerOptions = configuration.requestSchedulerOptions;
      this.hintedHandoffConfiguration = configuration.hintedHandoffConfiguration;
      this.commitLogConfiguration = configuration.commitLogConfiguration;
      this.performanceConfiguration = configuration.performanceConfiguration;
      this.rpcConfiguration = configuration.rpcConfiguration;
      this.compactionConfiguration = configuration.compactionConfiguration;
      this.snitchConfiguration = configuration.snitchConfiguration;
    }

    public Builder clusterName(@Nullable String clusterName) {
      this.clusterName = clusterName;
      return this;
    }

    public Builder partitioner(@Nullable Class<? extends IPartitioner<?>> partitioner) {
      this.partitioner = partitioner;
      return this;
    }

    public Builder seedProvider(@Nullable Class<? extends SeedProvider> seedProvider) {
      this.seedProvider = seedProvider;
      return this;
    }

    public Builder seedProviderOptions(Map<String, String> seedProviderOptions) {
      this.seedProviderOptions = ImmutableMap.copyOf(seedProviderOptions);
      return this;
    }

    public Builder dataFileDirectories(Set<File> dataFileDirectories) {
      this.dataFileDirectories = ImmutableSet.copyOf(dataFileDirectories);
      return this;
    }

    public Builder savedCachesDirectory(@Nullable File savedCachesDirectory) {
      this.savedCachesDirectory = savedCachesDirectory;
      return this;
    }

    public Builder initialToken(@Nullable BigInteger initialToken) {
      this.initialToken = Optional.fromNullable(initialToken);
      return this;
    }

    public Builder autoBootstrap(@Nullable Boolean autoBootstrap) {
      this.autoBootstrap = Optional.fromNullable(autoBootstrap);
      return this;
    }

    public Builder authenticator(@Nullable Class<? extends IAuthenticator> authenticator) {
      this.authenticator = Optional.<Class<? extends IAuthenticator>>fromNullable(authenticator);
      return this;
    }

    public Builder authority(@Nullable Class<? extends IAuthority> authority) {
      this.authority = Optional.<Class<? extends IAuthority>>fromNullable(authority);
      return this;
    }

    public Builder storagePort(@Nullable Integer storagePort) {
      this.storagePort = Optional.fromNullable(storagePort);
      return this;
    }

    public Builder listenAddress(@Nullable HostSpecifier listenAddress) {
      this.listenAddress = Optional.fromNullable(listenAddress);
      return this;
    }

    public Builder incrementalBackups(@Nullable Boolean incrementalBackups) {
      this.incrementalBackups = Optional.fromNullable(incrementalBackups);
      return this;
    }

    public Builder requestScheduler(@Nullable Class<? extends IRequestScheduler> requestScheduler) {
      this.requestScheduler = Optional.<Class<? extends IRequestScheduler>>fromNullable(requestScheduler);
      return this;
    }

    public Builder requestSchedulerOptions(@Nullable RequestSchedulerOptions requestSchedulerOptions) {
      this.requestSchedulerOptions = Optional.fromNullable(requestSchedulerOptions);
      return this;
    }

    public Builder hintedHandoffConfiguration(@Nullable HintedHandoffConfiguration configuration) {
      this.hintedHandoffConfiguration = configuration;
      return this;
    }

    public Builder commitLogConfiguration(@Nullable CommitLogConfiguration commitLogConfiguration) {
      this.commitLogConfiguration = commitLogConfiguration;
      return this;
    }

    public Builder performanceConfiguration(@Nullable PerformanceConfiguration performanceConfiguration) {
      this.performanceConfiguration = performanceConfiguration;
      return this;
    }

    public Builder rpcConfiguration(@Nullable RpcConfiguration rpcConfiguration) {
      this.rpcConfiguration = rpcConfiguration;
      return this;
    }

    public Builder compactionConfiguration(@Nullable CompactionConfiguration compactionConfiguration) {
      this.compactionConfiguration = compactionConfiguration;
      return this;
    }

    public Builder snitchConfiguration(@Nullable SnitchConfiguration snitchConfiguration) {
      this.snitchConfiguration = snitchConfiguration;
      return this;
    }

    public CassandraConfiguration build() {
      clusterName = Objects.firstNonNull(clusterName, "Test Cluster");
      partitioner = Objects.firstNonNull(partitioner, RandomPartitioner.class);
      seedProvider = Objects.firstNonNull(seedProvider, SimpleSeedProvider.class);
      if (seedProviderOptions == null || seedProviderOptions.isEmpty()) {
        seedProviderOptions = ImmutableMap.of("seeds", "127.0.0.1");
      }
      if (dataFileDirectories == null || dataFileDirectories.isEmpty()) {
        dataFileDirectories = ImmutableSet.of(new File("/var/lib/cassandra/data"));
      }
      savedCachesDirectory = Objects.firstNonNull(savedCachesDirectory,
          new File("/var/lib/cassandra/saved_caches"));
      hintedHandoffConfiguration = Objects.firstNonNull(hintedHandoffConfiguration,
          HintedHandoffConfiguration.DEFAULT);
      commitLogConfiguration = Objects.firstNonNull(commitLogConfiguration,
          CommitLogConfiguration.DEFAULT);
      performanceConfiguration = Objects.firstNonNull(performanceConfiguration,
          PerformanceConfiguration.DEFAULT);
      rpcConfiguration = Objects.firstNonNull(rpcConfiguration,
          RpcConfiguration.DEFAULT);
      compactionConfiguration = Objects.firstNonNull(compactionConfiguration,
          CompactionConfiguration.DEFAULT);
      snitchConfiguration = Objects.firstNonNull(snitchConfiguration,
          SnitchConfiguration.DEFAULT);
      if (!autoBootstrap.isPresent()) {
        // Doc says default false: http://wiki.apache.org/cassandra/StorageConfiguration
        // Code says default true: org.apache.cassandra.config.Config
        autoBootstrap = Optional.of(false);
      }
      return new CassandraConfiguration(this);
    }

  }

}

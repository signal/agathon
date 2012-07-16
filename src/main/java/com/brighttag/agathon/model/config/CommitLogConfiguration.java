package com.brighttag.agathon.model.config;

import java.io.File;
import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.joda.time.Period;

/**
 * Configuration options related to the commit log.
 *
 * @author codyaray
 * @since 6/27/12
 */
public class CommitLogConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final CommitLogConfiguration DEFAULT = new Builder().build();

  /**
   * Describes how the CommitLog will be synced to disk.
   */
  public enum SyncMode {
    /**
     * When in periodic mode, writes may be acked immediately and the CommitLog is
     * simply synced every {@link CommitLogConfiguration#syncPeriod syncPeriod}.
     */
    PERIODIC,

    /**
     * When in batch mode, Cassandra won't ack writes until the commit log has been fsynced
     * to disk. It will wait up to {@link CommitLogConfiguration#syncBatchWindow syncBatchWindow}
     * for other writes, before performing the sync.
     */
    BATCH
  };

  /**
   * Directory where Cassandra should store its commitlog. Defaults to "/var/lib/cassandra/commitlog".
   */
  private final File directory;

  /**
   * Describes how the CommitLog will be synced to disk. Defaults to {@link SyncMode#PERIODIC}.
   */
  private final SyncMode syncMode;

  /**
   * Required if {@link #syncMode} is {@link SyncMode#PERIODIC}. Defaults to 10 seconds.
   * See {@link SyncMode#PERIODIC} for details.
   */
  private final Optional<Period> syncPeriod;

  /**
   * Required if {@link #syncMode} is {@link SyncMode#BATCH}.
   * See {@link SyncMode#BATCH} for details.
   */
  private final Optional<Duration> syncBatchWindow;

  /**
   * Size to allow commitlog to grow to before creating a new segment.
   */
  private final Optional<DataSize> rotation;

  private CommitLogConfiguration(Builder builder) {
    this.directory = builder.directory;
    this.syncMode = builder.syncMode;
    this.syncBatchWindow = builder.syncBatchWindow;
    this.syncPeriod = builder.syncPeriod;
    this.rotation = builder.rotation;
  }

  public File getDirectory() {
    return directory;
  }

  public SyncMode getSyncMode() {
    return syncMode;
  }

  public Optional<Duration> getSyncBatchWindow() {
    return syncBatchWindow;
  }

  public Optional<Period> getSyncPeriod() {
    return syncPeriod;
  }

  public Optional<DataSize> getRotation() {
    return rotation;
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
        .add("directory", directory)
        .add("syncMode", syncMode)
        .add("syncBatchWindow", syncBatchWindow)
        .add("syncPeriod", syncPeriod)
        .add("rotation", rotation)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] { directory, syncMode, syncBatchWindow, syncPeriod, rotation };
  }

  /**
   * Fluent builder for {@link CommitLogConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    /*
     * Required attributes with defaults set in build()
     */
    private @Nullable File directory;
    private @Nullable SyncMode syncMode;

    /*
     * Optional attributes
     */
    private Optional<Period> syncPeriod = Optional.absent();
    private Optional<Duration> syncBatchWindow = Optional.absent();
    private Optional<DataSize> rotation = Optional.absent();

    public Builder directory(@Nullable File directory) {
      this.directory = directory;
      return this;
    }

    public Builder syncMode(@Nullable SyncMode syncMode) {
      this.syncMode = syncMode;
      return this;
    }

    public Builder syncBatchWindow(@Nullable Duration syncBatchWindow) {
      this.syncBatchWindow = Optional.fromNullable(syncBatchWindow);
      return this;
    }

    public Builder syncPeriod(@Nullable Period syncPeriod) {
      this.syncPeriod = Optional.fromNullable(syncPeriod);
      return this;
    }

    public Builder rotation(@Nullable DataSize rotation) {
      this.rotation = Optional.fromNullable(rotation);
      return this;
    }

    public CommitLogConfiguration build() {
      if (directory == null) {
        directory = new File("/var/lib/cassandra/commitlog");
      }
      if (syncMode == null) {
        // Doc says default PERIODIC: http://wiki.apache.org/cassandra/StorageConfiguration
        // Code says no default: org.apache.cassandra.config.DatabaseDescriptor
        syncMode = SyncMode.PERIODIC;
      }
      if (!syncPeriod.isPresent()) {
        // Doc says default PERIODIC: http://wiki.apache.org/cassandra/StorageConfiguration
        // Code says no default: org.apache.cassandra.config.DatabaseDescriptor
        syncPeriod = Optional.of(Period.seconds(10));
      }
      return new CommitLogConfiguration(this);
    }

  }

}

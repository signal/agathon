package com.brighttag.agathon.model.config;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.proofpoint.units.DataSize;

/**
 * Configuration options related to compaction.
 *
 * @author codyaray
 * @since 6/27/12
 */
public class CompactionConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final CompactionConfiguration DEFAULT = new Builder().build();

  /**
   * Whether or not to take a snapshot before each compaction. Be
   * careful using this option, since Cassandra won't clean up the
   * snapshots for you. Mostly useful if you're paranoid when there
   * is a data format change.
   */
  private final Optional<Boolean> snapshotBeforeCompaction;

  /**
   * Change this to increase the compaction thread's priority.  In java, 1 is the
   * lowest priority and 5 is the highest allowed.
   */

  private final Optional<Integer> threadPriority; // min=1, max=5 annotations

  /**
   * Size limit for rows being compacted in memory.  Larger rows will spill
   * over to disk and use a slower two-pass compaction process.  A message
   * will be logged specifying the row key.
   */
  private final Optional<DataSize> inMemoryLimit;

  /**
   * Number of simultaneous compactions to allow, NOT including validation "compactions"
   * for anti-entropy repair. This defaults to the number of cores. This can help preserve
   * read performance in a mixed read/write workload, by mitigating the tendency of small
   * sstables to accumulate during a single long running compactions. The default is usually
   * fine and if you experience problems with compaction running too slowly or too fast,
   * you should look at {@link #throughputMbPerSec} first.
   */
  private final Optional<Integer> concurrentCompactors;

  /**
   * Throttles compaction to the given total throughput across the entire system. The faster
   * you insert data, the faster you need to compact in order to keep the sstable count down,
   * but in general, setting this to 16 to 32 times the rate you are inserting data is more
   * than sufficient. Setting this to 0 disables throttling. Note that this account for all
   * types of compaction, including validation compaction.
   */
  private final Optional<Integer> throughputMbPerSec; // better datatype for throughput?

  /**
   * Track cached row keys during compaction, and re-cache their new positions in the
   * compacted sstable. Disable if you use really large key caches.
   */
  private final Optional<Boolean> preheatKeyCache;

  private CompactionConfiguration(Builder builder) {
    this.snapshotBeforeCompaction = builder.snapshotBeforeCompaction;
    this.threadPriority = builder.threadPriority;
    this.inMemoryLimit = builder.inMemoryLimit;
    this.concurrentCompactors = builder.concurrentCompactors;
    this.throughputMbPerSec = builder.throughputMbPerSec;
    this.preheatKeyCache = builder.preheatKeyCache;
  }

  public Optional<Boolean> getSnapshotBeforeCompaction() {
    return snapshotBeforeCompaction;
  }

  public Optional<Integer> getThreadPriority() {
    return threadPriority;
  }

  public Optional<DataSize> getInMemoryLimit() {
    return inMemoryLimit;
  }

  public Optional<Integer> getConcurrentCompactors() {
    return concurrentCompactors;
  }

  public Optional<Integer> getThroughputMbPerSec() {
    return throughputMbPerSec;
  }

  public Optional<Boolean> getPreheatKeyCache() {
    return preheatKeyCache;
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
        .add("snapshotBeforeCompaction", snapshotBeforeCompaction)
        .add("threadPriority", threadPriority)
        .add("inMemoryLimit", inMemoryLimit)
        .add("concurrentCompactors", concurrentCompactors)
        .add("throughputMbPerSec", throughputMbPerSec)
        .add("preheatKeyCache", preheatKeyCache)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {
      snapshotBeforeCompaction, threadPriority, inMemoryLimit,
      concurrentCompactors, throughputMbPerSec, preheatKeyCache,
    };
  }

  /**
   * Fluent builder for {@link CompactionConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    private Optional<Boolean> snapshotBeforeCompaction = Optional.absent();
    private Optional<Integer> threadPriority = Optional.absent();
    private Optional<DataSize> inMemoryLimit = Optional.absent();
    private Optional<Integer> concurrentCompactors = Optional.absent();
    private Optional<Integer> throughputMbPerSec = Optional.absent(); // better datatype?
    private Optional<Boolean> preheatKeyCache = Optional.absent();

    public Builder snapshotBeforeCompaction(@Nullable Boolean snapshotBeforeCompaction) {
      this.snapshotBeforeCompaction = Optional.fromNullable(snapshotBeforeCompaction);
      return this;
    }

    public Builder threadPriority(@Nullable Integer compactionThreadPriority) {
      this.threadPriority = Optional.fromNullable(compactionThreadPriority);
      return this;
    }

    public Builder inMemoryLimit(@Nullable DataSize inMemoryCompactionLimit) {
      this.inMemoryLimit = Optional.fromNullable(inMemoryCompactionLimit);
      return this;
    }

    public Builder concurrentCompactors(@Nullable Integer concurrentCompactors) {
      this.concurrentCompactors = Optional.fromNullable(concurrentCompactors);
      return this;
    }

    public Builder throughputMbPerSec(@Nullable Integer compactionThroughputMbPerSec) {
      this.throughputMbPerSec = Optional.fromNullable(compactionThroughputMbPerSec);
      return this;
    }

    public Builder preheatKeyCache(@Nullable Boolean compactionPreheatKeyCache) {
      this.preheatKeyCache = Optional.fromNullable(compactionPreheatKeyCache);
      return this;
    }

    public CompactionConfiguration build() {
      return new CompactionConfiguration(this);
    }

  }

}

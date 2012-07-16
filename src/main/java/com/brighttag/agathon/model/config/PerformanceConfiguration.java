package com.brighttag.agathon.model.config;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.proofpoint.units.DataSize;

/**
 * Configuration options related to Cassandra's performance.
 *
 * @author codyaray
 * @since 6/27/12
 */
public class PerformanceConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final PerformanceConfiguration DEFAULT = new Builder().build();

  /**
   * Emergency pressure valve: each time heap usage after a full (CMS) garbage collection
   * is above this fraction of the max, Cassandra will flush the largest memtables.
   *
   * Set to 1.0 to disable. Setting this lower than CMSInitiatingOccupancyFraction
   * is not likely to be useful.
   *
   * RELYING ON THIS AS YOUR PRIMARY TUNING MECHANISM WILL WORK POORLY:
   * it is most effective under light to moderate load, or read-heavy workloads; under truly
   * massive write load, it will often be too little, too late.
   */
  private final Optional<Double> flushLargestMemtablesAtHeapUsageFraction;

  /**
   * Emergency pressure valve #2: the first time heap usage after a full (CMS) garbage
   * collection is above this fraction of the max, Cassandra will reduce cache maximum
   * <i>capacity</i> to a {@link #reduceCacheCapacityToCurrentSizeFraction fraction}
   * of the current <i>size</i>. Should usually be set substantially above
   * {@link #flushLargestMemtablesAtHeapUsageFraction}, since that will have less long-term
   * impact on the system.
   *
   * Set to 1.0 to disable. Setting this lower than CMSInitiatingOccupancyFraction
   * is not likely to be useful.
   */
  private final Optional<Double> reduceCacheSizesAtHeapUsageFraction;

  /**
   * Emergency pressure valve #2: the first time heap usage after a full (CMS) garbage
   * collection is above {@link #reduceCacheSizesAtHeapUsageFraction fraction} of the max,
   * Cassandra will reduce cache maximum <i>capacity</i> to this fraction of the current
   * <i>size</i>.
   *
   * Set to 1.0 to disable.
   */
  private final Optional<Double> reduceCacheCapacityToCurrentSizeFraction;

  /**
   * For workloads with more data than can fit in memory, Cassandra's bottleneck will be
   * reads that need to fetch data from disk. This should be set to (16 * number_of_drives)
   * in order to allow the operations to enqueue low enough in the stack that the OS and
   * drives can reorder them.
   */
  private final Optional<Integer> concurrentReads;

  /**
   * Since writes are almost never IO bound, the ideal number of concurrentWrites is dependent
   * on the number of cores in your system; (8 * number_of_cores) is a good rule of thumb.
   */
  private final Optional<Integer> concurrentWrites;

  /**
   * Total memory to use for memtables.  Cassandra will flush the largest memtable
   * when this much memory is used. If omitted, Cassandra will set it to 1/3 of the heap.
   * Set to 0 to disable.
   */
  private final Optional<DataSize> memtableTotalSpace;

  /**
   * This sets the number of memtable flush writer threads.  These will be blocked
   * by disk io, and each one will hold a memtable in memory while blocked. If you
   * have a large heap and many data directories, you can increase this value for
   * better flush performance. By default this will be set to the number of data
   * directories defined.
   */
  private final Optional<Integer> memtableFlushWriters;

  /**
   * The number of full memtables to allow pending flush, that is, waiting for a writer
   * thread. At a minimum, this should be set to the maximum number of secondary indexes
   * created on a single CF.
   */
  private final Optional<Integer> memtableFlushQueueSize;

  /**
   * Buffer size to use when performing contiguous column slices. Increase this
   * to the size of the column slices you typically perform
   */
  private final Optional<DataSize> slicedBufferSize;

  /**
   * Frame size for thrift (maximum field length). 0 disables TFramedTransport in favor
   * of TSocket. This option is deprecated; we strongly recommend using Framed mode.
   */
  private final Optional<DataSize> thriftFramedTransportSize;

  /**
   * The max length of a thrift message, including all fields and internal thrift overhead.
   */
  private final Optional<DataSize> thriftMaxMessageSize;

  /**
   * Add column indexes to a row after its contents reach this size. Increase if your column
   * values are large, or if you have a very large number of columns.  The competing causes
   * are, Cassandra has to deserialize this much of the row to read a single column, so you
   * want it to be small - at least if you do many partial-row reads - but all the index data
   * is read for each access, so you don't want to generate that wastefully either.
   */
  private final Optional<DataSize> columnIndexSize;

  /**
   * Phi value that must be reached for a host to be marked down.
   * most users should never need to adjust this.
   */
  private final Optional<Integer> phiConvictThreshold;

  private PerformanceConfiguration(Builder builder) {
    this.flushLargestMemtablesAtHeapUsageFraction = builder.flushLargestMemtablesAtHeapUsageFraction;
    this.reduceCacheSizesAtHeapUsageFraction = builder.reduceCacheSizesAtHeapUsageFraction;
    this.reduceCacheCapacityToCurrentSizeFraction = builder.reduceCacheCapacityToCurrentSizeFraction;
    this.concurrentReads = builder.concurrentReads;
    this.concurrentWrites = builder.concurrentWrites;
    this.memtableTotalSpace = builder.memtableTotalSpace;
    this.memtableFlushWriters = builder.memtableFlushWriters;
    this.memtableFlushQueueSize = builder.memtableFlushQueueSize;
    this.slicedBufferSize = builder.slicedBufferSize;
    this.thriftFramedTransportSize = builder.thriftFramedTransportSize;
    this.thriftMaxMessageSize = builder.thriftMaxMessageSize;
    this.columnIndexSize = builder.columnIndexSize;
    this.phiConvictThreshold = builder.phiConvictThreshold;
  }

  public Optional<Double> getFlushLargestMemtablesAtHeapUsageFraction() {
    return flushLargestMemtablesAtHeapUsageFraction;
  }

  public Optional<Double> getReduceCacheSizesAtHeapUsageFraction() {
    return reduceCacheSizesAtHeapUsageFraction;
  }

  public Optional<Double> getReduceCacheCapacityToCurrentSizeFraction() {
    return reduceCacheCapacityToCurrentSizeFraction;
  }

  public Optional<Integer> getConcurrentReads() {
    return concurrentReads;
  }

  public Optional<Integer> getConcurrentWrites() {
    return concurrentWrites;
  }

  public Optional<DataSize> getMemtableTotalSpace() {
    return memtableTotalSpace;
  }

  public Optional<Integer> getMemtableFlushWriters() {
    return memtableFlushWriters;
  }

  public Optional<Integer> getMemtableFlushQueueSize() {
    return memtableFlushQueueSize;
  }

  public Optional<DataSize> getSlicedBufferSize() {
    return slicedBufferSize;
  }

  public Optional<DataSize> getThriftFramedTransportSize() {
    return thriftFramedTransportSize;
  }

  public Optional<DataSize> getThriftMaxMessageSize() {
    return thriftMaxMessageSize;
  }

  public Optional<DataSize> getColumnIndexSize() {
    return columnIndexSize;
  }

  public Optional<Integer> getPhiConvictThreshold() {
    return phiConvictThreshold;
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
        .add("flushLargestMemtablesAtHeapUsageFraction", flushLargestMemtablesAtHeapUsageFraction)
        .add("reduceCacheSizesAtHeapUsageFraction", reduceCacheSizesAtHeapUsageFraction)
        .add("reduceCacheCapacityToCurrentSizeFraction", reduceCacheCapacityToCurrentSizeFraction)
        .add("concurrentReads", concurrentReads)
        .add("concurrentWrites", concurrentWrites)
        .add("memtableTotalSpace", memtableTotalSpace)
        .add("memtableFlushWriters", memtableFlushWriters)
        .add("memtableFlushQueueSize", memtableFlushQueueSize)
        .add("slicedBufferSize", slicedBufferSize)
        .add("thriftFramedTransportSize", thriftFramedTransportSize)
        .add("thriftMaxMessageSize", thriftMaxMessageSize)
        .add("columnIndexSize", columnIndexSize)
        .add("phiConvictThreshold", phiConvictThreshold)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {
      flushLargestMemtablesAtHeapUsageFraction, reduceCacheSizesAtHeapUsageFraction,
      reduceCacheCapacityToCurrentSizeFraction, concurrentReads, concurrentWrites,
      memtableTotalSpace, memtableFlushWriters, memtableFlushQueueSize, slicedBufferSize,
      thriftFramedTransportSize, thriftMaxMessageSize, columnIndexSize, phiConvictThreshold,
    };
  }

  /**
   * Fluent builder for {@link PerformanceConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    private Optional<Double> flushLargestMemtablesAtHeapUsageFraction = Optional.absent();
    private Optional<Double> reduceCacheSizesAtHeapUsageFraction = Optional.absent();
    private Optional<Double> reduceCacheCapacityToCurrentSizeFraction = Optional.absent();
    private Optional<Integer> concurrentReads = Optional.absent();
    private Optional<Integer> concurrentWrites = Optional.absent();
    private Optional<DataSize> memtableTotalSpace = Optional.absent();
    private Optional<Integer> memtableFlushWriters = Optional.absent();
    private Optional<Integer> memtableFlushQueueSize = Optional.absent();
    private Optional<DataSize> slicedBufferSize = Optional.absent();
    private Optional<DataSize> thriftFramedTransportSize = Optional.absent();
    private Optional<DataSize> thriftMaxMessageSize = Optional.absent();
    private Optional<DataSize> columnIndexSize = Optional.absent();
    private Optional<Integer> phiConvictThreshold = Optional.absent();

    public Builder flushLargestMemtablesAtHeapUsageFraction(
        @Nullable Double flushLargestMemtablesAtHeapUsageFraction) {
      this.flushLargestMemtablesAtHeapUsageFraction =
          Optional.fromNullable(flushLargestMemtablesAtHeapUsageFraction);
      return this;
    }

    public Builder reduceCacheSizesAtHeapUsageFraction(
        @Nullable Double reduceCacheSizesAtHeapUsageFraction) {
      this.reduceCacheSizesAtHeapUsageFraction =
          Optional.fromNullable(reduceCacheSizesAtHeapUsageFraction);
      return this;
    }

    public Builder reduceCacheCapacityToCurrentSizeFraction(
        @Nullable Double reduceCacheCapacityToCurrentSizeFraction) {
      this.reduceCacheCapacityToCurrentSizeFraction =
          Optional.fromNullable(reduceCacheCapacityToCurrentSizeFraction);
      return this;
    }

    public Builder concurrentReads(@Nullable Integer concurrentReads) {
      this.concurrentReads = Optional.fromNullable(concurrentReads);
      return this;
    }

    public Builder concurrentWrites(@Nullable Integer concurrentWrites) {
      this.concurrentWrites = Optional.fromNullable(concurrentWrites);
      return this;
    }

    public Builder memtableTotalSpace(@Nullable DataSize memtableTotalSpace) {
      this.memtableTotalSpace = Optional.fromNullable(memtableTotalSpace);
      return this;
    }

    public Builder memtableFlushWriters(@Nullable Integer memtableFlushWriters) {
      this.memtableFlushWriters = Optional.fromNullable(memtableFlushWriters);
      return this;
    }

    public Builder memtableFlushQueueSize(@Nullable Integer memtableFlushQueueSize) {
      this.memtableFlushQueueSize = Optional.fromNullable(memtableFlushQueueSize);
      return this;
    }

    public Builder slicedBufferSize(@Nullable DataSize slicedBufferSize) {
      this.slicedBufferSize = Optional.fromNullable(slicedBufferSize);
      return this;
    }

    public Builder thriftFramedTransportSize(@Nullable DataSize thriftFramedTransportSize) {
      this.thriftFramedTransportSize = Optional.fromNullable(thriftFramedTransportSize);
      return this;
    }

    public Builder thriftMaxMessageSize(@Nullable DataSize thriftMaxMessageSize) {
      this.thriftMaxMessageSize = Optional.fromNullable(thriftMaxMessageSize);
      return this;
    }

    public Builder columnIndexSize(@Nullable DataSize columnIndexSize) {
      this.columnIndexSize = Optional.fromNullable(columnIndexSize);
      return this;
    }

    public Builder phiConvictThreshold(@Nullable Integer phiConvictThreshold) {
      this.phiConvictThreshold = Optional.fromNullable(phiConvictThreshold);
      return this;
    }

    public PerformanceConfiguration build() {
      return new PerformanceConfiguration(this);
    }

  }

}

package com.brighttag.agathon.model.config;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.apache.cassandra.locator.IEndpointSnitch;
import org.apache.cassandra.locator.SimpleSnitch;
import org.joda.time.Period;

/**
 * Configuration options related to the endpoint snitch.
 *
 * @author codyaray
 * @since 6/27/12
 */
public class SnitchConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final SnitchConfiguration DEFAULT = new Builder().build();

  /**
   * Backend used to let Cassandra know enough about your network topology
   * to route requests efficiently. Defaults to {@link SimpleSnitch}.
   */
  private final Class<? extends IEndpointSnitch> endpointSnitch;

  /**
   * Whether the {@link #endpointSnitch} is wrapped with a dynamic snitch, which
   * will monitor read latencies and avoid reading from hosts that have slowed
   * (due to compaction, for instance).
   */
  private final Optional<Boolean> dynamicSnitch;

  /**
   * How often to perform the more expensive part of host score calculation.
   */
  private final Optional<Period> dynamicSnitchUpdateInterval;

  /**
   * How often to reset all host scores, allowing a bad host to possibly recover.
   */
  private final Optional<Period> dynamicSnitchResetInterval;

  /**
   * If set to greater than zero and read_repair_chance is < 1.0, this will allow
   * 'pinning' of replicas to hosts in order to increase cache capacity. The badness
   * threshold will control how much worse the pinned host has to be before the dynamic
   * snitch will prefer other replicas over it. This is expressed as a double which
   * represents a percentage. Thus, a value of 0.2 means Cassandra would continue to
   * prefer the static snitch values until the pinned host was 20% worse than the fastest.
   */
  private final Optional<Double> dynamicSnitchBadnessThreshold;

  private SnitchConfiguration(Builder builder) {
    this.endpointSnitch = builder.endpointSnitch;
    this.dynamicSnitch = builder.dynamicSnitch;
    this.dynamicSnitchUpdateInterval = builder.dynamicSnitchUpdateInterval;
    this.dynamicSnitchResetInterval = builder.dynamicSnitchResetInterval;
    this.dynamicSnitchBadnessThreshold = builder.dynamicSnitchBadnessThreshold;
  }

  public Class<? extends IEndpointSnitch> getEndpointSnitch() {
    return endpointSnitch;
  }

  public Optional<Boolean> getDynamicSnitch() {
    return dynamicSnitch;
  }

  public Optional<Period> getDynamicSnitchUpdateInterval() {
    return dynamicSnitchUpdateInterval;
  }

  public Optional<Period> getDynamicSnitchResetInterval() {
    return dynamicSnitchResetInterval;
  }

  public Optional<Double> getDynamicSnitchBadnessThreshold() {
    return dynamicSnitchBadnessThreshold;
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
        .add("endpointSnitch", endpointSnitch)
        .add("dynamicSnitch", dynamicSnitch)
        .add("dynamicSnitchUpdateInterval", dynamicSnitchUpdateInterval)
        .add("dynamicSnitchResetInterval", dynamicSnitchResetInterval)
        .add("dynamicSnitchBadnessThreshold", dynamicSnitchBadnessThreshold)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {
      endpointSnitch, dynamicSnitch, dynamicSnitchUpdateInterval,
      dynamicSnitchResetInterval, dynamicSnitchBadnessThreshold,
    };
  }

  /**
   * Fluent builder for {@link SnitchConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    /*
     * Required attributes with defaults set in build()
     */
    private @Nullable Class<? extends IEndpointSnitch> endpointSnitch;

    /*
     * Optional attributes
     */
    private Optional<Boolean> dynamicSnitch = Optional.absent();
    private Optional<Period> dynamicSnitchUpdateInterval = Optional.absent();
    private Optional<Period> dynamicSnitchResetInterval = Optional.absent();
    private Optional<Double> dynamicSnitchBadnessThreshold = Optional.absent();

    public Builder() {
      // Nothing to do
    }

    public Builder(SnitchConfiguration configuration) {
      this.endpointSnitch = configuration.endpointSnitch;
      this.dynamicSnitch = configuration.dynamicSnitch;
      this.dynamicSnitchUpdateInterval = configuration.dynamicSnitchUpdateInterval;
      this.dynamicSnitchResetInterval = configuration.dynamicSnitchResetInterval;
      this.dynamicSnitchBadnessThreshold = configuration.dynamicSnitchBadnessThreshold;
    }

    public Builder endpointSnitch(@Nullable Class<? extends IEndpointSnitch> endpointSnitch) {
      this.endpointSnitch = endpointSnitch;
      return this;
    }

    public Builder dynamicSnitch(@Nullable Boolean dynamicSnitch) {
      this.dynamicSnitch = Optional.fromNullable(dynamicSnitch);
      return this;
    }

    public Builder dynamicSnitchUpdateInterval(@Nullable Period dynamicSnitchUpdateInterval) {
      this.dynamicSnitchUpdateInterval = Optional.fromNullable(dynamicSnitchUpdateInterval);
      return this;
    }

    public Builder dynamicSnitchResetInterval(@Nullable Period dynamicSnitchResetInterval) {
      this.dynamicSnitchResetInterval = Optional.fromNullable(dynamicSnitchResetInterval);
      return this;
    }

    public Builder dynamicSnitchBadnessThreshold(@Nullable Double dynamicSnitchBadnessThreshold) {
      this.dynamicSnitchBadnessThreshold = Optional.fromNullable(dynamicSnitchBadnessThreshold);
      return this;
    }

    public SnitchConfiguration build() {
      if (endpointSnitch == null) {
        // Doc says default SimpleSnitch: http://wiki.apache.org/cassandra/StorageConfiguration
        // Code says no default: org.apache.cassandra.config.DatabaseDescriptor
        endpointSnitch = SimpleSnitch.class;
      }
      return new SnitchConfiguration(this);
    }

  }

}

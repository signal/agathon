package com.brighttag.agathon.model.config;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.joda.time.Duration;

/**
 * Configuration options related to hinted handoff.
 *
 * @see <a href="http://wiki.apache.org/cassandra/HintedHandoff">Hinted Handoff</a>
 * @author codyaray
 * @since 6/27/12
 */
public class HintedHandoffConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final HintedHandoffConfiguration DEFAULT = new Builder().build();

  /**
   * Whether to enable hinted handoff.
   */
  private final Optional<Boolean> enabled;

  /**
   * The maximum amount of time a dead host will have hints generated.
   * After it has been dead this long, hints will be dropped.
   */
  private final Optional<Duration> maxHintWindow;

  /**
   * Sleep this long after delivering each row or row fragment.
   */
  private final Optional<Duration> throttleDelay;

  private HintedHandoffConfiguration(Builder builder) {
    this.enabled = builder.enabled;
    this.maxHintWindow = builder.maxHintWindow;
    this.throttleDelay = builder.throttleDelay;
  }

  public Optional<Boolean> getEnabled() {
    return enabled;
  }

  public Optional<Duration> getMaxHintWindow() {
    return maxHintWindow;
  }

  public Optional<Duration> getThrottleDelay() {
    return throttleDelay;
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
        .add("enabled", enabled)
        .add("maxHintWindow", maxHintWindow)
        .add("throttleDelay", throttleDelay)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] { enabled, maxHintWindow, throttleDelay };
  }

  /**
   * Fluent builder for {@link HintedHandoffConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    private Optional<Boolean> enabled = Optional.absent();
    private Optional<Duration> maxHintWindow = Optional.absent();
    private Optional<Duration> throttleDelay = Optional.absent();

    public Builder enabled(@Nullable Boolean enabled) {
      this.enabled = Optional.fromNullable(enabled);
      return this;
    }

    public Builder maxHintWindow(@Nullable Duration maxHintWindow) {
      this.maxHintWindow = Optional.fromNullable(maxHintWindow);
      return this;
    }

    public Builder throttleDelay(@Nullable Duration hintedHandoffThrottleDelay) {
      this.throttleDelay = Optional.fromNullable(hintedHandoffThrottleDelay);
      return this;
    }

    public HintedHandoffConfiguration build() {
      return new HintedHandoffConfiguration(this);
    }

  }

}

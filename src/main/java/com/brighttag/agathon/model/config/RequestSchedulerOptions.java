package com.brighttag.agathon.model.config;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable set of options for the {@link IRequestScheduler}.
 *
 * @author codyaray
 * @since 7/15/12
 */
public class RequestSchedulerOptions {

  private final int throttleLimit;
  private final Map<String, Integer> weights;
  private final Optional<Integer> defaultWeight;

  private RequestSchedulerOptions(Builder builder) {
    this.throttleLimit = builder.throttleLimit;
    this.weights = builder.weights;
    this.defaultWeight = builder.defaultWeight;
  }

  public int getThrottleLimit() {
    return throttleLimit;
  }

  public Map<String, Integer> getWeights() {
    return weights;
  }

  public Optional<Integer> getDefaultWeight() {
    return defaultWeight;
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
        .add("throttleLimit", throttleLimit)
        .add("weights", weights)
        .add("defaultWeight", defaultWeight)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] { throttleLimit, weights, defaultWeight };
  }

  /**
   * Fluent builder for {@link RequestSchedulerOptions}.
   *
   * @author codyaray
   * @since 7/15/12
   */
  public static class Builder {

    private Integer throttleLimit;
    private Map<String, Integer> weights;
    private Optional<Integer> defaultWeight = Optional.absent();

    public Builder throttleLimit(Integer throttleLimit) {
      this.throttleLimit = throttleLimit;
      return this;
    }

    public Builder weights(Map<String, Integer> weights) {
      this.weights = ImmutableMap.copyOf(weights);
      return this;
    }

    public Builder defaultWeight(@Nullable Integer defaultWeight) {
      this.defaultWeight = Optional.fromNullable(defaultWeight);
      return this;
    }

    public RequestSchedulerOptions build() {
      checkNotNull(throttleLimit);
      if (weights == null) {
        weights = ImmutableMap.of();
      }
      return new RequestSchedulerOptions(this);
    }

  }

}

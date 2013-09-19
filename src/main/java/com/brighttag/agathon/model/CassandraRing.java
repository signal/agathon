package com.brighttag.agathon.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import org.hibernate.validator.constraints.NotEmpty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable Cassandra ring.
 *
 * @author codyaray
 * @since 9/16/2013
 */
public class CassandraRing {

  private final String name;
  private final ImmutableSet<CassandraInstance> instances;

  private CassandraRing(@JsonProperty("name") String name,
      @JsonProperty("instances") Set<CassandraInstance> instances) {
    this(new Builder()
        .name(name)
        .instances(instances));
  }

  private CassandraRing(Builder builder) {
    this.name = builder.name;
    this.instances = builder.instances.build();
  }

  public @NotEmpty String getName() {
    return name;
  }

  public @NotEmpty ImmutableSet<CassandraInstance> getInstances() {
    return instances;
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
        .add("name", name)
        .add("instances", instances)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] { name, instances };
  }

  /**
   * Fluent builder for {@link CassandraRing}s.
   *
   * @author codyaray
   * @since 9/16/2013
   */
  public static class Builder {

    private String name;
    private final ImmutableSet.Builder<CassandraInstance> instances = ImmutableSet.builder();

    public Builder instances(Collection<CassandraInstance> instances) {
      this.instances.addAll(instances);
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public CassandraRing build() {
      checkNotNull(name);
      return new CassandraRing(this);
    }

  }

}

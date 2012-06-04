package com.brighttag.agathon.model;

import java.math.BigInteger;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * An immutable Cassandra instance. Instances are ordered by position
 * on the ring, indicated by token.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstance implements Comparable<CassandraInstance> {

  private final int id;
  private final String datacenter;
  private final String rack;
  private final String hostname;
  private final @Nullable BigInteger token;

  private CassandraInstance(
      @JsonProperty("id") int id, @JsonProperty("datacenter") String datacenter,
      @JsonProperty("rack") String rack, @JsonProperty("hostname") String hostname,
      @Nullable @JsonProperty("token") BigInteger token) {
    this.id = id;
    this.datacenter = datacenter;
    this.rack = rack;
    this.hostname = hostname;
    this.token = token;
  }

  private CassandraInstance(Builder builder) {
    this.id = builder.id;
    this.datacenter = builder.datacenter;
    this.rack = builder.rack;
    this.hostname = builder.hostname;
    this.token = builder.token;
  }

  public @Min(1) int getId() {
    return id;
  }

  public @NotEmpty String getDataCenter() {
    return datacenter;
  }

  public @NotEmpty String getRack() {
    return rack;
  }

  public @NotEmpty String getHostName() {
    return hostname;
  }

  public @Nullable @Min(0) BigInteger getToken() {
    return token;
  }

  @Override
  public int compareTo(CassandraInstance that) {
    // compare significant fields beyond the token so the ordering is consistent with equals
    return ComparisonChain.start()
        .compare(this.token, that.token, Ordering.natural().nullsLast())
        .compare(this.id, that.id)
        .compare(this.datacenter, that.datacenter)
        .compare(this.rack, that.rack)
        .compare(this.hostname, that.hostname)
        .result();
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
        .add("id", id)
        .add("datacenter", datacenter)
        .add("rack", rack)
        .add("hostname", hostname)
        .add("token", token)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {id, datacenter, rack, hostname, token};
  }

  /**
   * Fluent builder for {@link CassandraInstance}s.
   *
   * @author codyaray
   * @since 5/12/12
   */
  public static class Builder {

    private int id;
    private String datacenter;
    private String rack;
    private String hostname;
    private @Nullable BigInteger token;

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder dataCenter(String dataCenter) {
      this.datacenter = dataCenter;
      return this;
    }

    public Builder rack(String rack) {
      this.rack = rack;
      return this;
    }

    public Builder hostName(String hostName) {
      this.hostname = hostName;
      return this;
    }

    public Builder token(@Nullable BigInteger token) {
      this.token = token;
      return this;
    }

    public CassandraInstance build() {
      return new CassandraInstance(this);
    }

  }

}

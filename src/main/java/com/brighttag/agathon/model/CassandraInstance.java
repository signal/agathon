package com.brighttag.agathon.model;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

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

  private final String id;
  private final String token;
  private final String datacenter;
  private final String rack;
  private final String hostname;

  private CassandraInstance(
      @JsonProperty("id") String id, @JsonProperty("token") String token,
      @JsonProperty("datacenter") String datacenter, @JsonProperty("rack") String rack,
      @JsonProperty("hostname") String hostname) {
    this.id = id;
    this.token = token;
    this.datacenter = datacenter;
    this.rack = rack;
    this.hostname = hostname;
  }

  private CassandraInstance(Builder builder) {
    this.id = builder.id;
    this.token = builder.token;
    this.datacenter = builder.datacenter;
    this.rack = builder.rack;
    this.hostname = builder.hostname;
  }

  public @NotEmpty String getId() {
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

  public @NotEmpty String getToken() {
    return token;
  }

  @Override
  public int compareTo(CassandraInstance that) {
    // compare significant fields beyond the token so the ordering is consistent with equals
    return ComparisonChain.start()
        .compare(new BigInteger(this.token), new BigInteger(that.token))
        // temporary hack until we convert these to true integers
        .compare(Integer.parseInt(this.id), Integer.parseInt(that.id))
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
        .add("token", token)
        .add("datacenter", datacenter)
        .add("rack", rack)
        .add("hostname", hostname)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {id, token, datacenter, rack, hostname};
  }

  /**
   * Fluent builder for {@link CassandraInstance}s.
   *
   * @author codyaray
   * @since 5/12/12
   */
  public static class Builder {

    private String id;
    private String token;
    private String datacenter;
    private String rack;
    private String hostname;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder token(String token) {
      this.token = token;
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

    public CassandraInstance build() {
      return new CassandraInstance(this);
    }

  }

}

package com.brighttag.agathon.model;

import java.util.Arrays;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * An immutable Cassandra instance.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstance {

  private final int id;
  private final String datacenter;
  private final String rack;
  private final String hostname;
  private final String publicIpAddress;

  private CassandraInstance(
      @JsonProperty("id") int id, @JsonProperty("datacenter") String dataCenter,
      @JsonProperty("rack") String rack, @JsonProperty("hostname") String hostName,
      @JsonProperty("publicIpAddress") String publicIpAddress) {
    this(new Builder()
        .id(id)
        .dataCenter(dataCenter)
        .rack(rack)
        .hostName(hostName)
        .publicIpAddress(publicIpAddress));
  }

  private CassandraInstance(Builder builder) {
    this.id = builder.id;
    this.datacenter = builder.datacenter;
    this.rack = builder.rack;
    this.hostname = builder.hostname;
    this.publicIpAddress = builder.publicIpAddress;
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

  public @NotEmpty String getPublicIpAddress() {
    return publicIpAddress;
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
        .add("publicIpAddress", publicIpAddress)
        .toString();
  }

  Object[] significantAttributes() {
    return new Object[] { id, datacenter, rack, hostname, publicIpAddress };
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
    private String publicIpAddress;

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

    public Builder publicIpAddress(String publicIpAddress) {
      this.publicIpAddress = publicIpAddress;
      return this;
    }

    public CassandraInstance build() {
      return new CassandraInstance(this);
    }

  }

}

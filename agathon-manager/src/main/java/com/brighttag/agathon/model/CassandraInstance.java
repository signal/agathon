/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.model;

import java.util.Arrays;

import javax.annotation.Nullable;
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
  private final @Nullable String fullyQualifiedDomainName;

  private CassandraInstance(
      @JsonProperty("id") int id, @JsonProperty("datacenter") String dataCenter,
      @JsonProperty("rack") String rack, @JsonProperty("hostname") String hostName,
      @JsonProperty("publicIpAddress") String publicIpAddress,
      @JsonProperty("fullyQualifiedDomainName") String fullyQualifiedDomainName) {
    this(new Builder()
        .id(id)
        .dataCenter(dataCenter)
        .rack(rack)
        .hostName(hostName)
        .publicIpAddress(publicIpAddress)
        .fullyQualifiedDomainName(fullyQualifiedDomainName));
  }

  private CassandraInstance(Builder builder) {
    this.id = builder.id;
    this.datacenter = builder.datacenter;
    this.rack = builder.rack;
    this.hostname = builder.hostname;
    this.publicIpAddress = builder.publicIpAddress;
    this.fullyQualifiedDomainName = builder.fullyQualifiedDomainName;
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

  public @Nullable String getFullyQualifiedDomainName() {
    return fullyQualifiedDomainName;
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
        .add("fullyQualifiedDomainName", fullyQualifiedDomainName)
        .toString();
  }

  Object[] significantAttributes() {
    return new Object[] { id, datacenter, rack, hostname, publicIpAddress, fullyQualifiedDomainName };
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
    private @Nullable String fullyQualifiedDomainName;

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

    public Builder fullyQualifiedDomainName(@Nullable String fullyQualifiedDomainName) {
      this.fullyQualifiedDomainName = fullyQualifiedDomainName;
      return this;
    }

    public CassandraInstance build() {
      return new CassandraInstance(this);
    }

  }

}

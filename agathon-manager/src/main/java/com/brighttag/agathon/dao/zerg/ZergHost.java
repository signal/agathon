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

package com.brighttag.agathon.dao.zerg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Mutable representation of a host in Zerg.
 *
 * @author codyaray
 * @since 9/27/2013
 */
class ZergHost {

  private String name;
  private List<String> roles;
  private String zone;
  private @SerializedName("public ip") String publicIpAddress;
  private @SerializedName("fqdn") @Nullable String fullyQualifiedDomainName;

  ZergHost() { /* For Gson */ }

  @VisibleForTesting ZergHost(String name, List<String> roles, String zone,
      String publicIpAddress, @Nullable String fullyQualifiedDomainName) {
    this.name = name;
    this.roles = roles;
    this.zone = zone;
    this.publicIpAddress = publicIpAddress;
    this.fullyQualifiedDomainName = fullyQualifiedDomainName;
  }

  /**
   * Returns the unique identifier for this host.
   */
  public int getId() {
    // Specified by {@link ZergCassandraInstanceDao} because Zerg doesn't provide a stable unique ID
    return name.hashCode();
  }

  /**
   * Returns the host name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets a new host name.
   *
   * The JSON representation doesn't include the hostname as part of the entity. This entity
   * appears in a JSON dictionary and the hostname is its key, so we have to manually specify it
   * after Gson parses the rest of the entity.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the host's roles.
   */
  public List<String> getRoles() {
    return Collections.unmodifiableList(roles);
  }

  /**
   * Returns the host's zone.
   */
  public String getZone() {
    return zone;
  }

  /**
   * Returns the host's public IP address.
   */
  public String getPublicIpAddress() {
    return publicIpAddress;
  }

  /**
   * Returns the host's fully qualified domain name.
   */
  public @Nullable String getFullyQualifiedDomainName() {
    return fullyQualifiedDomainName;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("name", name)
        .add("roles", roles)
        .add("zone", zone)
        .add("publicIpAddress", publicIpAddress)
        .add("fullyQualifiedDomainName", fullyQualifiedDomainName)
        .toString();
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

  Object[] significantAttributes() {
    return new Object[] { name, roles, zone, publicIpAddress, fullyQualifiedDomainName };
  }

}

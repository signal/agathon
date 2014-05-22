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

package com.brighttag.agathon.security;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

/**
 * Immutable representation of a security group permission.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public class SecurityGroupPermission {
  private final ImmutableSet<Netmask> netmasks;
  private final Range<Integer> portRange;

  public SecurityGroupPermission(Collection<Netmask> netmasks, Range<Integer> portRange) {
    this.netmasks = ImmutableSet.copyOf(netmasks);
    this.portRange = portRange;
  }

  /**
   * Get the network masks included in this permission.
   * @return network masks
   */
  public ImmutableSet<Netmask> getNetmasks() {
    return netmasks;
  }

  /**
   * Get the range of ports included in this permission.
   * @return the range of ports
   */
  public Range<Integer> getPortRange() {
    return portRange;
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
        .add("netmasks", netmasks)
        .add("portRange", portRange)
        .toString();
  }

  Object[] significantAttributes() {
    return new Object[] { netmasks, portRange };
  }

}

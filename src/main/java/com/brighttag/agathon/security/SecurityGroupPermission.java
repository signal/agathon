package com.brighttag.agathon.security;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

/**
 * Represents a security group permission.
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

  private Object[] significantAttributes() {
    return new Object[] { netmasks, portRange };
  }

}


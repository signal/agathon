package com.brighttag.agathon.security;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

/**
 * Immutable implementation of {@link SecurityGroupPermission}.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public class SecurityGroupPermissionImpl implements SecurityGroupPermission {
  private final ImmutableSet<String> ipRanges;
  private final Range<Integer> portRange;

  public SecurityGroupPermissionImpl(Collection<String> ipRanges, Range<Integer> portRange) {
    this.ipRanges = ImmutableSet.copyOf(ipRanges);
    this.portRange = portRange;
  }

  @Override
  public ImmutableSet<String> getIpRanges() {
    return ipRanges;
  }

  @Override
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
        .add("ipRanges", ipRanges)
        .add("portRange", portRange)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] { ipRanges, portRange };
  }

}

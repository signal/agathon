package com.brighttag.agathon.security;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

/**
 * Represents a security group permission.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public interface SecurityGroupPermission {

  /**
   * Get the IP address ranges included in this permission in CIDR notation.
   * @return IP address ranges in CIDR notation.
   */
  public ImmutableSet<String> getIpRanges();

  /**
   * Get the range of ports included in this permission.
   * @return the range of ports
   */
  public Range<Integer> getPortRange();
}

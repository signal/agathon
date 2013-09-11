package com.brighttag.agathon.security;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;

/**
 * Represents a network mask.
 *
 * @author codyaray
 * @since 9/11/2013
 */
public class Netmask {

  private static final Function<Netmask, String> TO_CIDR = new Function<Netmask, String>() {
    @Override
    public String apply(Netmask netmask) {
      return netmask.toCIDR();
    }
  };

  private static final Function<String, Netmask> FROM_CIDR = new Function<String, Netmask>() {
    @Override
    public Netmask apply(String cidr) {
      return fromCIDR(cidr);
    }
  };

  private final String networkAddress;
  private final int routingPrefixLength;

  private Netmask(String networkAddress, int routingPrefixLength) {
    this.networkAddress = networkAddress;
    this.routingPrefixLength = routingPrefixLength;
  }

  public static @Nullable Netmask fromCIDR(String cidr) {
    String[] parts = cidr.split("/");
    if (parts.length == 2) {
      Integer prefixLength = Ints.tryParse(parts[1]);
      if (prefixLength != null) {
        return new Netmask(parts[0], prefixLength);
      }
    }
    return null;
  }

  public static ImmutableSet<Netmask> fromCIDR(Collection<String> cidrs) {
    return FluentIterable.from(cidrs).transform(FROM_CIDR).toSet();
  }

  public static ImmutableSet<String> toCidr(Collection<Netmask> netmasks) {
    return FluentIterable.from(netmasks).transform(TO_CIDR).toSet();
  }

  public String toCIDR() {
    return networkAddress + "/" + routingPrefixLength;
  }

  public String getNetworkAddress() {
    return networkAddress;
  }

  public int getRoutingPrefixLength() {
    return routingPrefixLength;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("networkAddress", networkAddress)
        .add("routingPrefixLength", routingPrefixLength)
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

  private Object[] significantAttributes() {
    return new Object[] { networkAddress, routingPrefixLength };
  }

}

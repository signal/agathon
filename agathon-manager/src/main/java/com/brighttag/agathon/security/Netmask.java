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

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;

/**
 * Represents a network mask.
 *
 * @author codyaray
 * @since 9/11/2013
 */
public class Netmask {

  private final String networkAddress;
  private final int routingPrefixLength;

  @VisibleForTesting Netmask(String networkAddress, int routingPrefixLength) {
    this.networkAddress = networkAddress;
    this.routingPrefixLength = routingPrefixLength;
  }

  public static @Nullable Netmask fromCidr(String cidr) {
    String[] parts = cidr.split("/");
    if (parts.length == 2 && InetAddresses.isInetAddress(parts[0])) {
      Integer prefixLength = Ints.tryParse(parts[1]);
      if (prefixLength != null) {
        return new Netmask(parts[0], prefixLength);
      }
    }
    return null;
  }

  public static ImmutableSet<Netmask> fromCidr(Collection<String> cidrs) {
    return FluentIterable.from(cidrs).transform(FROM_CIDR).filter(VALID_NETMASKS).toSet();
  }

  public static ImmutableSet<String> toCidr(Collection<Netmask> netmasks) {
    return FluentIterable.from(netmasks).transform(TO_CIDR).toSet();
  }

  public String toCidr() {
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
    return toCidr();
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
    return new Object[] { networkAddress, routingPrefixLength };
  }

  private static final Netmask INVALID_NETMASK = new Netmask("null", 0);

  private static final Function<Netmask, String> TO_CIDR = new Function<Netmask, String>() {
    @Override
    public String apply(Netmask netmask) {
      return netmask.toCidr();
    }
  };

  private static final Function<String, Netmask> FROM_CIDR = new Function<String, Netmask>() {
    @Override
    public Netmask apply(String cidr) {
      return Objects.firstNonNull(fromCidr(cidr), INVALID_NETMASK);
    }
  };

  private static final Predicate<Netmask> VALID_NETMASKS = new Predicate<Netmask>() {
    @Override
    public boolean apply(Netmask netmask) {
      return !INVALID_NETMASK.equals(netmask);
    }
  };

}

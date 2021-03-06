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

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * A collection of {@link ZergHost} objects. There is some collective behavior, which is why we've
 * modeled this as an object instead of just passing around a {@code Set&lt;ZergHost&gt;}.
 *
 * For example, you can get all the CassandraInstances in the "userstats" ring:
 * <pre>
 *   ZergHosts.from(zergConnector.getHosts()).filterRing("userstats").toCassandraInstances();
 * </pre>
 *
 * To find the "userstats" ring in the "us-east-1" region when there's a ring per region:
 * <pre>
 *   ZergHosts.from(zergConnector.getHosts())
 *     .filterRing("userstats")
 *     .filterRegion("us-east-1")
 *     .toCassandraInstances();
 * </pre>
 *
 * @author codyaray
 * @since 9/27/2013
 */
class ZergHosts {

  private static final String CASSANDRA_RING_ROLE_PREFIX = "cassandra_";

  private final ImmutableSet<ZergHost> hosts;

  private ZergHosts(ImmutableSet<ZergHost> hosts) {
    this.hosts = hosts;
  }

  /**
   * Creates a ZergHosts set from a collection of {@link ZergHost}s.
   *
   * @param hosts the hosts to be in this set
   * @return the ZergHosts
   */
  public static ZergHosts from(Collection<ZergHost> hosts) {
    return new ZergHosts(ImmutableSet.copyOf(hosts));
  }

  private static final Pattern ZONE_PATTERN = Pattern.compile("^(\\w+-\\w+)-(\\w+)$");

  /**
   * Transforms the {@link ZergHost host} into a {@link CassandraInstance}.
   *
   * @param host the zerg host
   * @return the Cassandra instance
   */
  public static @Nullable CassandraInstance toCassandraInstance(ZergHost host) {
    /*
     * Transform from Zerg zone to Cassandra DataCenter/Rack.
     * Zerg's zone is a combination of AWS region and availability zone.
     * Assumes the {@link Ec2Snitch} or {@link Ec2MultiRegionSnitch}
     * For example, Zone: "us-east-1a" => DC: "us-east", Rack: "1a"
     */
    Matcher m = ZONE_PATTERN.matcher(host.getZone());
    if (m.find()) {
      return new CassandraInstance.Builder()
          .id(host.getId())
          .dataCenter(m.group(1))
          .rack(m.group(2))
          .hostName(host.getName())
          .publicIpAddress(host.getPublicIpAddress())
          .fullyQualifiedDomainName(host.getFullyQualifiedDomainName())
          .build();
    }
    return null;
  }

  /**
   * Returns the names of all rings in this set.
   *
   * @return the names of all rings in this set
   */
  public ImmutableSet<String> rings() {
    ImmutableSet.Builder<String> rings = ImmutableSet.builder();
    for (ZergHost host : hosts) {
      for (String role : Iterables.filter(host.getRoles(), startsWith(CASSANDRA_RING_ROLE_PREFIX))) {
        rings.add(role.substring(CASSANDRA_RING_ROLE_PREFIX.length()));
      }
    }
    return rings.build();
  }

  /**
   * Returns the elements of this set that are in the {@code ring}.
   *
   * @param ring the desired ring
   */
  public ZergHosts filterRing(final String ring) {
    return from(FluentIterable.from(hosts).filter(new Predicate<ZergHost>() {
      @Override
      public boolean apply(ZergHost host) {
        return host.getRoles().contains(role(ring));
      }
    })
    .toSet());
  }

  /**
   * Returns the elements of this set that are in the {@code region}.
   *
   * @param region the desired region
   */
  public ZergHosts filterRegion(final String region) {
    return from(FluentIterable.from(hosts).filter(new Predicate<ZergHost>() {
      @Override
      public boolean apply(ZergHost host) {
        return host.getZone().startsWith(region);
      }
    })
    .toSet());
  }

  /**
   * Returns the elements of this set that are in the {@code ring} and have the right scope
   * (either scoped to the {@code region} or environment as defined by {@code ringScopes}).
   *
   * @param ringScopes map of ring names to scopes (one of "environment" or "region")
   * @param region the desired region
   * @param ring the desired ring
   */
  public ZergHosts filterScope(Map<String, String> ringScopes, String region, String ring) {
    ZergHosts hosts = filterRing(ring);
    if ("region".equals(ringScopes.get(ring))) {
      hosts = hosts.filterRegion(region);
    }
    return hosts;
  }

  /**
   * Returns an {@link ImmutableSet} containing all the elements from this set with duplicates removed.
   */
  public ImmutableSet<ZergHost> toSet() {
    return hosts;
  }

  /**
   * Transforms this set of {@link ZergHost}s into {@link CassandraInstance}s.
   *
   * @return the corresponding set of {@link CassandraInstance}s
   */
  public ImmutableSet<CassandraInstance> toCassandraInstances() {
    return FluentIterable.from(hosts).transform(TO_CASSANDRA_INSTANCE).filter(VALID_INSTANCE).toSet();
  }

  private static String role(String ring) {
    return CASSANDRA_RING_ROLE_PREFIX + ring;
  }

  private static Predicate<String> startsWith(final String prefix) {
    return new Predicate<String>() {
      @Override
      public boolean apply(@Nullable String input) {
        return input != null && input.startsWith(prefix);
      }
    };
  }

  private static final CassandraInstance INVALID_INSTANCE = new CassandraInstance.Builder().build();

  private static final Function<ZergHost, CassandraInstance> TO_CASSANDRA_INSTANCE =
      new Function<ZergHost, CassandraInstance>() {
        @Override
        public CassandraInstance apply(ZergHost host) {
          return Objects.firstNonNull(toCassandraInstance(host), INVALID_INSTANCE);
        }
      };

  private static final Predicate<CassandraInstance> VALID_INSTANCE = new Predicate<CassandraInstance>() {
    @Override
    public boolean apply(CassandraInstance instance) {
      return !INVALID_INSTANCE.equals(instance);
    }
  };

}

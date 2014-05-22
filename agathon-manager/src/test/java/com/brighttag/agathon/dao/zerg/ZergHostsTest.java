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
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 9/27/2013
 */
public class ZergHostsTest {

  @Test
  public void toCassandraInstance() {
    assertEquals(instance("host1", "us-east", "1a", "1.1.1.1", "domain1"),
        ZergHosts.toCassandraInstance(host("host1", "us-east-1a", "1.1.1.1", "domain1")));
  }

  @Test
  public void toCassandraInstance_noAvailabilityZone() {
    assertNull(ZergHosts.toCassandraInstance(host("host1", "us-east", "1.1.1.1", "domain1")));
  }

  @Test
  public void toCassandraInstance_emptyAvailabilityZone() {
    assertEquals(null, ZergHosts.toCassandraInstance(host("host1", "us-east-", "1.1.1.1", "domain1")));
  }

  @Test
  public void toCassandraInstance_invalidRegion() {
    assertNull(ZergHosts.toCassandraInstance(host("host1", "us", "1.1.1.1", "domain1")));
  }

  @Test
  public void toCassandraInstance_emptyRegion() {
    assertNull(ZergHosts.toCassandraInstance(host("host1", "-1a", "1.1.1.1", "domain1")));
  }

  @Test
  public void rings() {
    assertEquals(ImmutableSet.of("ring1", "ring2", "ring3"), ZergHosts.from(HOSTS).rings());
  }

  @Test
  public void rings_noHosts() {
    assertEquals(ImmutableSet.of(), ZergHosts.from(ImmutableSet.<ZergHost>of()).rings());
  }

  @Test
  public void rings_ignoresNonCassandraRoles() {
    assertEquals(ImmutableSet.of("ring1"), ZergHosts.from(ImmutableSet.of(HOST1, HOST4)).rings());
  }

  @Test
  public void filter() {
    assertEquals(ImmutableSet.of(HOST1, HOST3), ZergHosts.from(HOSTS).filterRing("ring1").toSet());
    assertEquals(ImmutableSet.of(HOST2, HOST3), ZergHosts.from(HOSTS).filterRing("ring2").toSet());
    assertEquals(ImmutableSet.of(HOST3), ZergHosts.from(HOSTS).filterRing("ring3").toSet());
  }

  @Test
  public void filter_unknownRing() {
    assertEquals(ImmutableSet.of(), ZergHosts.from(HOSTS).filterRing("whatsthis").toSet());
  }

  @Test
  public void filter_noHosts() {
    assertEquals(ImmutableSet.of(), ZergHosts.from(ImmutableSet.<ZergHost>of()).filterRing("ring1").toSet());
  }

  @Test
  public void toCassandraInstances() {
    assertEquals(INSTANCES, ZergHosts.from(HOSTS).toCassandraInstances());
  }

  @Test
  public void toCassandraInstances_skipsInvalidHost() {
    Set<ZergHost> hosts = ImmutableSet.of(host("host1", "us-east-1a", "1.1.1.1", "domain1"),
        host("host2", "invalid", "2.2.2.2", "domain2"), host("host3", "eu-west-3c", "3.3.3.3", "domain3"));
    assertEquals(ImmutableSet.of(INSTANCE1, INSTANCE3), ZergHosts.from(hosts).toCassandraInstances());
  }

  @Test
  public void toSet() {
    assertEquals(HOSTS, ZergHosts.from(HOSTS).toSet());
  }

  @Test
  public void toSet_removesDuplicates() {
    List<ZergHost> hosts = Lists.newArrayList(HOSTS);
    hosts.add(HOST1);
    assertEquals(HOSTS, ZergHosts.from(hosts).toSet());
  }

  private static final ZergHost HOST1 = host("host1", "us-east-1a", "1.1.1.1", "domain1", "cassandra_ring1");
  private static final ZergHost HOST2 = host("host2", "us-west-2b", "2.2.2.2", "domain2", "cassandra_ring2");
  private static final ZergHost HOST3 = host("host3", "eu-west-3c", "3.3.3.3", "domain3",
      "cassandra_ring1", "cassandra_ring2", "cassandra_ring3");
  private static final ZergHost HOST4 = host("host4", "ap-northeast-4d", "4.4.4.4", "domain4",
      "cassandra_ring1", "not_cassandra_ring", "other_ring");
  private static final Collection<ZergHost> HOSTS = ImmutableSet.of(HOST1, HOST2, HOST3);

  private static final CassandraInstance INSTANCE1 = instance("host1", "us-east", "1a", "1.1.1.1", "domain1");
  private static final CassandraInstance INSTANCE2 = instance("host2", "us-west", "2b", "2.2.2.2", "domain2");
  private static final CassandraInstance INSTANCE3 = instance("host3", "eu-west", "3c", "3.3.3.3", "domain3");
  private static final Set<CassandraInstance> INSTANCES = ImmutableSet.of(INSTANCE1, INSTANCE2, INSTANCE3);

  private static ZergHost host(String hostName, String zone, String publicIp,
      String domainName, String... roles) {
    return new ZergHost(hostName, ImmutableList.copyOf(roles), zone, publicIp, domainName);
  }

  private static CassandraInstance instance(String name, String dataCenter, String rack,
      String publicIp, @Nullable String fqdn) {
    return new CassandraInstance.Builder()
        .id(name.hashCode())
        .hostName(name)
        .dataCenter(dataCenter)
        .rack(rack)
        .publicIpAddress(publicIp)
        .fullyQualifiedDomainName(fqdn)
        .build();
  }

}

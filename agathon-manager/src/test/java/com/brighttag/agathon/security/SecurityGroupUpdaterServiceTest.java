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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import org.easymock.EasyMockSupport;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.ServiceUnavailableException;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author Greg Opaczewski
 * @author codyaray
 * @since 1/11/2013
 */
public class SecurityGroupUpdaterServiceTest extends EasyMockSupport {

  private CassandraRingService cassandraRingService;
  private SecurityGroupService securityGroupService;

  @Before
  public void setUp() {
    cassandraRingService = createMock(CassandraRingService.class);
    securityGroupService = createMock(SecurityGroupService.class);
  }

  @After
  public void verifyMocks() {
    verifyAll();
  }

  @Test
  public void listGroupRules_noneExisting() {
    securityGroupStartingRules("dc1");
    replayAll();
    assertEquals(ImmutableSet.<Netmask>of(), service().listGroupRules("cassandra_ringName", "dc1", 7000));
  }

  @Test
  public void listGroupRules() {
    securityGroupStartingRules("dc1", groupPermission(8888, "111.0.0.0/8"),
        groupPermission(7000, "222.0.0.0/8"), groupPermission(7000, "1.1.1.1/32"));
    replayAll();
    assertEquals(Netmask.fromCidr(Arrays.asList("222.0.0.0/8", "1.1.1.1/32")),
        service().listGroupRules("cassandra_ringName", "dc1", 7000));
  }

  @Test
  public void requiredRulesFor_emptyList() {
    replayAll();
    assertEquals(ImmutableSet.<Netmask>of(), service().requiredRulesFor(
        ImmutableList.<CassandraInstance>of()));
  }

  @Test
  public void requiredRulesFor() {
    replayAll();
    assertEquals(Netmask.fromCidr(Arrays.asList("1.1.1.1/32", "2.2.2.2/32")), service().requiredRulesFor(
        ImmutableList.of(instance("1.1.1.1", null), instance("2.2.2.2", null))));
  }

  @Test
  public void runOneIteration_noRings() throws Exception {
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.<CassandraRing>of());
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_noExistingGroupsOrRules() throws Exception {
    securityGroupStartingRules("dc1");
    securityGroupStartingRules("dc2");
    expect(securityGroupService.exists("cassandra_ringName", "dc1")).andReturn(false);
    expect(securityGroupService.exists("cassandra_ringName", "dc2")).andReturn(false);
    securityGroupService.create("cassandra_ringName", "dc1");
    securityGroupService.create("cassandra_ringName", "dc2");
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.of(ringWithInstances(
        instance("1.1.1.1", "dc1"), instance("2.2.2.2", "dc2"))));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc1",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc2",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_missingNecessaryRulesInAllRegions() throws Exception {
    securityGroupStartingRules("dc1", groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc2", groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc3", groupPermission(7000, "2.2.2.2/32"));
    expect(securityGroupService.exists("cassandra_ringName", "dc1")).andReturn(true);
    expect(securityGroupService.exists("cassandra_ringName", "dc2")).andReturn(true);
    expect(securityGroupService.exists("cassandra_ringName", "dc3")).andReturn(true);
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.of(ringWithInstances(
        instance("1.1.1.1", "dc1"), instance("2.2.2.2", "dc2"), instance("3.3.3.3", "dc3"))));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc1",
        groupPermission(7000, "1.1.1.1/32", "3.3.3.3/32"));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc2",
        groupPermission(7000, "1.1.1.1/32", "3.3.3.3/32"));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc3",
        groupPermission(7000, "1.1.1.1/32", "3.3.3.3/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_missingNecessaryRulesInOnlyOneRegion() throws Exception {
    securityGroupStartingRules("dc1",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc2",
        groupPermission(7000, "2.2.2.2/32"));
    expect(securityGroupService.exists("cassandra_ringName", "dc1")).andReturn(true);
    expect(securityGroupService.exists("cassandra_ringName", "dc2")).andReturn(true);
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.of(ringWithInstances(
        instance("1.1.1.1", "dc1"), instance("2.2.2.2", "dc2"))));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc2",
        groupPermission(7000, "1.1.1.1/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_addAndRemoveFromMultipleRings() throws Exception {
    expect(securityGroupService.exists("cassandra_ringName", "dc1")).andReturn(true);
    expect(securityGroupService.exists("cassandra_ringName", "dc2")).andReturn(true);
    expect(securityGroupService.exists("cassandra_ringName", "dc3")).andReturn(true);
    expect(securityGroupService.exists("cassandra_otherRing", "dc1")).andReturn(true);
    expect(securityGroupService.exists("cassandra_otherRing", "dc2")).andReturn(true);
    expect(securityGroupService.exists("cassandra_otherRing", "dc4")).andReturn(true);
    securityGroupStartingRules("ringName", "dc1",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupStartingRules("ringName", "dc2",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "3.3.3.3/32"),
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupStartingRules("ringName", "dc3",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "3.3.3.3/32"));
    securityGroupStartingRules("otherRing", "dc1",
        groupPermission(7000, "5.5.5.5/32"),
        groupPermission(7000, "6.6.6.6/32"),
        groupPermission(7000, "8.8.8.8/32"));
    securityGroupStartingRules("otherRing", "dc2",
        groupPermission(7000, "5.5.5.5/32"),
        groupPermission(7000, "6.6.6.6/32"),
        groupPermission(7000, "7.7.7.7/32"),
        groupPermission(7000, "8.8.8.8/32"));
    securityGroupStartingRules("otherRing", "dc4",
        groupPermission(7000, "5.5.5.5/32"),
        groupPermission(7000, "6.6.6.6/32"),
        groupPermission(7000, "7.7.7.7/32"));
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.of(
        ringWithInstances("ringName",
          instance("1.1.1.1", "dc1"), instance("2.2.2.2", "dc2"), instance("3.3.3.3", "dc3")),
        ringWithInstances("otherRing",
          instance("5.5.5.5", "dc1"), instance("6.6.6.6", "dc2"), instance("7.7.7.7", "dc4"))));
    securityGroupService.authorizeIngressRules("cassandra_ringName", "dc1",
        groupPermission(7000, "3.3.3.3/32"));
    securityGroupService.revokeIngressRules("cassandra_ringName", "dc1",
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupService.revokeIngressRules("cassandra_ringName", "dc2",
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupService.authorizeIngressRules("cassandra_otherRing", "dc1",
        groupPermission(7000, "7.7.7.7/32"));
    securityGroupService.revokeIngressRules("cassandra_otherRing", "dc1",
        groupPermission(7000, "8.8.8.8/32"));
    securityGroupService.revokeIngressRules("cassandra_otherRing", "dc2",
        groupPermission(7000, "8.8.8.8/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_oneRingWithSslPort7001() throws Exception {
    securityGroupStartingRules("secure", "dc1");
    securityGroupStartingRules("secure", "dc2");
    expect(securityGroupService.exists("cassandra_secure", "dc1")).andReturn(false);
    expect(securityGroupService.exists("cassandra_secure", "dc2")).andReturn(false);
    securityGroupService.create("cassandra_secure", "dc1");
    securityGroupService.create("cassandra_secure", "dc2");
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.of(ringWithInstances(
        "secure", instance("1.1.1.1", "dc1"), instance("2.2.2.2", "dc2"))));
    securityGroupService.authorizeIngressRules("cassandra_secure", "dc1",
        groupPermission(7001, "1.1.1.1/32", "2.2.2.2/32"));
    securityGroupService.authorizeIngressRules("cassandra_secure", "dc2",
        groupPermission(7001, "1.1.1.1/32", "2.2.2.2/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_serviceUnavailableException() throws Exception {
    expect(cassandraRingService.findAll()).andThrow(new ServiceUnavailableException());
    replayAll();
    service().runOneIteration();
  }

  private static final Function<CassandraInstance, String> ASSIGNED_DATA_CENTER =
      new Function<CassandraInstance, String>() {
        @Override
        public String apply(CassandraInstance instance) {
          return instance.getDataCenter();
        }
      };

  private SecurityGroupUpdaterService service() {
    return new SecurityGroupUpdaterService(cassandraRingService, securityGroupService,
        ASSIGNED_DATA_CENTER, 7000, 7001, ImmutableSet.of("secure"),
        Duration.standardSeconds(60), "cassandra_");
  }

  private CassandraRing ringWithInstances(CassandraInstance... instances) {
    return ringWithInstances("ringName", instances);
  }

  private CassandraRing ringWithInstances(String ring, CassandraInstance... instances) {
    return new CassandraRing.Builder().name(ring).instances(Arrays.asList(instances)).build();
  }

  private CassandraInstance instance(String publicIp, String dataCenter) {
    return new CassandraInstance.Builder().publicIpAddress(publicIp).dataCenter(dataCenter).build();
  }

  private void securityGroupStartingRules(String dataCenter, SecurityGroupPermission... permissions) {
    securityGroupStartingRules("ringName", dataCenter, permissions);
  }

  private void securityGroupStartingRules(String ring, String dataCenter,
      SecurityGroupPermission... permissions) {
    expect(securityGroupService.getPermissions("cassandra_" + ring, dataCenter))
        .andReturn(ImmutableSet.copyOf(Arrays.asList(permissions)));
  }

  private static SecurityGroupPermission groupPermission(int port, String... ipRules) {
    return new SecurityGroupPermission(Netmask.fromCidr(Arrays.asList(ipRules)), Range.singleton(port));
  }

}

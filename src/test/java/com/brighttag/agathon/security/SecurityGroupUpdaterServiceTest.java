package com.brighttag.agathon.security;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;

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
    assertEquals(ImmutableSet.of(), service().listGroupRules("cassandra_ringName", "dc1"));
  }

  @Test
  public void listGroupRules() {
    securityGroupStartingRules("dc1", groupPermission(8888, "111.0.0.0/8"),
        groupPermission(7000, "222.0.0.0/8"), groupPermission(7000, "1.1.1.1/32"));
    replayAll();
    assertEquals(Netmask.fromCidr(Arrays.asList("222.0.0.0/8", "1.1.1.1/32")),
        service().listGroupRules("cassandra_ringName", "dc1"));
  }

  @Test
  public void requiredRulesFor_emptyList() {
    replayAll();
    assertEquals(ImmutableSet.of(), service().requiredRulesFor(
        ImmutableList.<CassandraInstance>of()));
  }

  @Test
  public void requiredRulesFor() {
    replayAll();
    assertEquals(Netmask.fromCidr(Arrays.asList("1.1.1.1/32", "2.2.2.2/32")), service().requiredRulesFor(
        ImmutableList.of(instance("1.1.1.1", null), instance("2.2.2.2", null))));
  }

  @Test
  public void runOneIteration_noRings() {
    expect(cassandraRingService.findAll()).andReturn(ImmutableSet.<CassandraRing>of());
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_noExistingGroupsOrRules() {
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
  public void runOneIteration_missingNecessaryRulesInAllRegions() {
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
  public void runOneIteration_missingNecessaryRulesInOnlyOneRegion() {
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
  public void runOneIteration_addAndRemoveFromMultipleRings() {
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

  private static final Function<CassandraInstance, String> ASSIGNED_DATA_CENTER =
      new Function<CassandraInstance, String>() {
        @Override
        public String apply(CassandraInstance instance) {
          return instance.getDataCenter();
        }
      };

  private SecurityGroupUpdaterService service() {
    return new SecurityGroupUpdaterService(cassandraRingService, securityGroupService,
        ASSIGNED_DATA_CENTER, 7000, 60);
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

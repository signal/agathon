package com.brighttag.agathon.security;

import java.util.Arrays;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author Greg Opaczewski
 * @author codyaray
 * @since 1/11/2013
 */
public class SecurityGroupUpdaterServiceTest extends EasyMockSupport {

  private CassandraInstanceService cassandraInstanceService;
  private SecurityGroupService securityGroupService;

  @Before
  public void setUp() {
    cassandraInstanceService = createMock(CassandraInstanceService.class);
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
    assertEquals(ImmutableSet.of(), service().listGroupRules("dc1"));
  }

  @Test
  public void listGroupRules() {
    securityGroupStartingRules("dc1", groupPermission(8888, "111.0.0.0/8"),
        groupPermission(7000, "222.0.0.0/8"), groupPermission(7000, "1.1.1.1/32"));
    replayAll();
    assertEquals(ImmutableSet.of("222.0.0.0/8", "1.1.1.1/32"), service().listGroupRules("dc1"));
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
    assertEquals(ImmutableSet.of("1.1.1.1/32", "2.2.2.2/32"), service().requiredRulesFor(
        ImmutableList.of(new CassandraInstance.Builder().publicIpAddress("1.1.1.1").build(),
            new CassandraInstance.Builder().publicIpAddress("2.2.2.2").build())));
  }

  @Test
  public void runOneIteration_noMembersInRing() {
    securityGroupStartingRules("dc1");
    securityGroupStartingRules("dc2");
    securityGroupStartingRules("dc3");
    expect(cassandraInstanceService.findAll()).andReturn(ImmutableList.<CassandraInstance>of());
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_noExistingRules() {
    securityGroupStartingRules("dc1");
    securityGroupStartingRules("dc2");
    securityGroupStartingRules("dc3");
    expect(cassandraInstanceService.findAll()).andReturn(ImmutableList.of(
        new CassandraInstance.Builder().publicIpAddress("1.1.1.1").build(),
        new CassandraInstance.Builder().publicIpAddress("2.2.2.2").build()));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc1",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc2",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc3",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_missingNecessaryRulesInAllRegions() {
    securityGroupStartingRules("dc1", groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc2", groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc3", groupPermission(7000, "2.2.2.2/32"));
    expect(cassandraInstanceService.findAll()).andReturn(ImmutableList.of(
        new CassandraInstance.Builder().publicIpAddress("1.1.1.1").build(),
        new CassandraInstance.Builder().publicIpAddress("2.2.2.2").build(),
        new CassandraInstance.Builder().publicIpAddress("3.3.3.3").build()));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc1",
        groupPermission(7000, "1.1.1.1/32", "3.3.3.3/32"));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc2",
        groupPermission(7000, "1.1.1.1/32", "3.3.3.3/32"));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc3",
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
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"));
    securityGroupStartingRules("dc3",
        groupPermission(7000, "2.2.2.2/32"));
    expect(cassandraInstanceService.findAll()).andReturn(ImmutableList.of(
        new CassandraInstance.Builder().publicIpAddress("1.1.1.1").build(),
        new CassandraInstance.Builder().publicIpAddress("2.2.2.2").build()));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc3",
        groupPermission(7000, "1.1.1.1/32"));
    replayAll();
    service().runOneIteration();
  }

  @Test
  public void runOneIteration_addAndRemove() {
    securityGroupStartingRules("dc1",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupStartingRules("dc2",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "3.3.3.3/32"),
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupStartingRules("dc3",
        groupPermission(7000, "1.1.1.1/32"),
        groupPermission(7000, "2.2.2.2/32"),
        groupPermission(7000, "3.3.3.3/32"));
    expect(cassandraInstanceService.findAll()).andReturn(ImmutableList.of(
        new CassandraInstance.Builder().publicIpAddress("1.1.1.1").build(),
        new CassandraInstance.Builder().publicIpAddress("2.2.2.2").build(),
        new CassandraInstance.Builder().publicIpAddress("3.3.3.3").build()));
    securityGroupService.authorizeIngressRules("securityGroupName", "dc1",
        groupPermission(7000, "3.3.3.3/32"));
    securityGroupService.revokeIngressRules("securityGroupName", "dc1",
        groupPermission(7000, "4.4.4.4/32"));
    securityGroupService.revokeIngressRules("securityGroupName", "dc2",
        groupPermission(7000, "4.4.4.4/32"));
    replayAll();
    service().runOneIteration();
  }

  private SecurityGroupUpdaterService service() {
    return new SecurityGroupUpdaterService(cassandraInstanceService, securityGroupService,
        ImmutableList.of("dc1", "dc2", "dc3"), "securityGroupName", 7000, 60);
  }

  private void securityGroupStartingRules(String dataCenter, SecurityGroupPermission... permissions) {
    expect(securityGroupService.getPermissions("securityGroupName", dataCenter))
        .andReturn(ImmutableSet.copyOf(Arrays.asList(permissions)));
  }

  private static SecurityGroupPermission groupPermission(int port, String... ipRule) {
    return new SecurityGroupPermissionImpl(Arrays.asList(ipRule), Range.singleton(port));
  }

}

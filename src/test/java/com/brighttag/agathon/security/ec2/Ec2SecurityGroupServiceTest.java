package com.brighttag.agathon.security.ec2;

import java.util.Arrays;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.inject.util.Providers;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.brighttag.agathon.security.Netmask;
import com.brighttag.agathon.security.SecurityGroupPermission;
import com.brighttag.agathon.security.SecurityGroupService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 8/30/2013
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Region.class)
public class Ec2SecurityGroupServiceTest extends EasyMockSupport {

  private AmazonEC2 amazonEc2;
  private Region region;
  private Map<String, Region> regions;

  @Before
  public void setUp() {
    amazonEc2 = createMock(AmazonEC2.class);
    region = PowerMock.createMock(Region.class);
    regions = ImmutableMap.of("dc1", region);
  }

  @After
  public void verifyMocks() {
    verifyAll();
  }

  @Test
  public void exists_false() {
    securityGroupStartingRules();
    replayAll();
    assertFalse(service().exists("thisDoesntExist", "dc1"));
  }

  @Test
  public void exists_true() {
    securityGroupStartingRules();
    replayAll();
    assertTrue(service().exists("securityGroupName", "dc1"));
  }

  @Test
  public void getPermissions_groupDoesNotExist() {
    amazonEc2.setRegion(region);
    expect(amazonEc2.describeSecurityGroups())
        .andReturn(new DescribeSecurityGroupsResult()
          .withSecurityGroups(ImmutableList.<SecurityGroup>of()));
    replayAll();
    assertEquals(ImmutableSet.of(), service().getPermissions("securityGroupName", "dc1"));
  }

  @Test
  public void getPermissions_noneExisting() {
    securityGroupStartingRules();
    replayAll();
    assertEquals(ImmutableSet.of(), service().getPermissions("securityGroupName", "dc1"));
  }

  @Test
  public void getPermissions() {
    securityGroupStartingRules(ipPermission(8888, "111.0.0.0/8"), ipPermission(7000, "222.0.0.0/8"));
    replayAll();
    assertEquals(ImmutableSet.of(groupPermission(8888, "111.0.0.0/8"), groupPermission(7000, "222.0.0.0/8")),
        service().getPermissions("securityGroupName", "dc1"));
  }

  @Test
  public void authorizeIngressRules() {
    amazonEc2.setRegion(region);
    amazonEc2.authorizeSecurityGroupIngress(new AuthorizeSecurityGroupIngressRequest(
        "securityGroupName", ImmutableList.of(ipPermission(7000, "1.1.1.1/32", "2.2.2.2/32"))));
    replayAll();
    service().authorizeIngressRules("securityGroupName", "dc1",
        groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"));
  }

  @Test
  public void revokeIngressRules() {
    amazonEc2.setRegion(region);
    amazonEc2.revokeSecurityGroupIngress(new RevokeSecurityGroupIngressRequest(
        "securityGroupName", ImmutableList.of(ipPermission(7000, "4.4.4.4/32"))));
    replayAll();
    service().revokeIngressRules("securityGroupName", "dc1", groupPermission(7000, "4.4.4.4/32"));
  }

  private SecurityGroupService service() {
    return new Ec2SecurityGroupService(Providers.of(amazonEc2), regions);
  }

  private void securityGroupStartingRules(IpPermission... ipPermissions) {
    amazonEc2.setRegion(region);
    SecurityGroup group = new SecurityGroup()
      .withGroupName("securityGroupName").withIpPermissions(ipPermissions);
    expect(amazonEc2.describeSecurityGroups()).andReturn(
        new DescribeSecurityGroupsResult().withSecurityGroups(ImmutableList.of(group)));
  }

  private static IpPermission ipPermission(int port, String... ipRules) {
    return new IpPermission().withIpProtocol("tcp").withIpRanges(ipRules).withFromPort(port).withToPort(port);
  }

  private static SecurityGroupPermission groupPermission(int port, String... ipRules) {
    return new SecurityGroupPermission(Netmask.fromCIDR(Arrays.asList(ipRules)), Range.singleton(port));
  }

  @Override
  public void replayAll() {
    super.replayAll();
    PowerMock.replayAll();
  }

  @Override
  public void verifyAll() {
    super.verifyAll();
    PowerMock.verifyAll();
  }

}

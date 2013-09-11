package com.brighttag.agathon.security.ec2;

import java.util.Arrays;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import com.brighttag.agathon.security.Netmask;
import com.brighttag.agathon.security.SecurityGroupModule;
import com.brighttag.agathon.security.SecurityGroupPermission;
import com.brighttag.agathon.security.SecurityGroupService;

/**
 * Manages EC2 security groups.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public class Ec2SecurityGroupService implements SecurityGroupService {

  private final Provider<AmazonEC2> amazonEC2Provider;
  private final Map<String, Region> regions;

  @Inject
  public Ec2SecurityGroupService(Provider<AmazonEC2> amazonEC2Provider,
      @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY) Map<String, Region> regions) {
    this.amazonEC2Provider = amazonEC2Provider;
    this.regions = ImmutableMap.copyOf(regions);
  }

  @Override
  public boolean exists(String groupName, String dataCenter) {
    return getSecurityGroup(groupName, dataCenter).isPresent();
  }

  @Override
  public void create(String groupName, String dataCenter) {
    client(dataCenter).createSecurityGroup(new CreateSecurityGroupRequest()
        .withGroupName(groupName)
        // description is required for EC2
        .withDescription("Agathon Cassandra Group"));
  }

  @Override
  public ImmutableSet<SecurityGroupPermission> getPermissions(String groupName, String dataCenter) {
    Optional<SecurityGroup> securityGroup = getSecurityGroup(groupName, dataCenter);
    if (securityGroup.isPresent()) {
      return fromIpPermissions(securityGroup.get().getIpPermissions());
    }
    return ImmutableSet.of();
  }

  @Override
  public void authorizeIngressRules(String groupName, String dataCenter, SecurityGroupPermission permission) {
    client(dataCenter).authorizeSecurityGroupIngress(
        new AuthorizeSecurityGroupIngressRequest(groupName, toIpPermissions(permission)));
  }

  @Override
  public void revokeIngressRules(String groupName, String dataCenter, SecurityGroupPermission permission) {
    client(dataCenter).revokeSecurityGroupIngress(
        new RevokeSecurityGroupIngressRequest(groupName, toIpPermissions(permission)));
  }

  private Optional<SecurityGroup> getSecurityGroup(String groupName, String dataCenter) {
    DescribeSecurityGroupsResult result = client(dataCenter).describeSecurityGroups();
    return Iterables.tryFind(result.getSecurityGroups(), withGroupName(groupName));
  }

  private static Predicate<SecurityGroup> withGroupName(final String groupName) {
    return new Predicate<SecurityGroup>() {
      @Override
      public boolean apply(SecurityGroup securityGroup) {
        return securityGroup.getGroupName().equals(groupName);
      }
    };
  }

  /**
   * Converts EC2 {@link IpPermission}s to {@link SecurityGroupPermission}s.
   */
  private ImmutableSet<SecurityGroupPermission> fromIpPermissions(Iterable<IpPermission> permissions) {
    return FluentIterable.from(permissions)
        .transform(new Function<IpPermission, SecurityGroupPermission>() {
          @Override
          public SecurityGroupPermission apply(IpPermission permission) {
            return new SecurityGroupPermission(Netmask.fromCIDR(permission.getIpRanges()),
                Range.closed(permission.getFromPort(), permission.getToPort()));
          }
        })
        .toSet();
  }

  /**
   * Converts {@link SecurityGroupPermission}s to EC2 {@link IpPermission}s.
   */
  private ImmutableList<IpPermission> toIpPermissions(Iterable<SecurityGroupPermission> permissions) {
    return FluentIterable.from(permissions)
        .transform(new Function<SecurityGroupPermission, IpPermission>() {
          @Override
          public IpPermission apply(SecurityGroupPermission permission) {
            return new IpPermission().withIpProtocol("tcp")
                .withFromPort(permission.getPortRange().lowerEndpoint())
                .withToPort(permission.getPortRange().upperEndpoint())
                .withIpRanges(Netmask.toCidr(permission.getNetmasks()));
          }
        })
        .toList();
  }

  private ImmutableList<IpPermission> toIpPermissions(SecurityGroupPermission... permissions) {
    return toIpPermissions(Arrays.asList(permissions));
  }

  private AmazonEC2 client(String dataCenter) {
    AmazonEC2 client = amazonEC2Provider.get();
    client.setRegion(regions.get(dataCenter));
    return client;
  }

}

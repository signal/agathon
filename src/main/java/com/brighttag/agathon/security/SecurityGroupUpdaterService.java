package com.brighttag.agathon.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically updates the security group associated with the Cassandra ring.
 *
 * @author Greg Opaczewski
 * @author codyaray
 * @since 1/11/2013
 */
public class SecurityGroupUpdaterService extends AbstractScheduledService {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityGroupUpdaterService.class);

  private final CassandraInstanceService cassandraInstanceService;
  private final SecurityGroupService securityGroupService;
  private final String securityGroupName;
  private final int listenPort;
  private final int updatePeriod;

  private final Iterable<String> dataCenters;

  @Inject
  public SecurityGroupUpdaterService(
      CassandraInstanceService cassandraInstanceService,
      SecurityGroupService securityGroupService,
      @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY) Collection<String> dataCenters,
      @Named(SecurityGroupModule.SECURITY_GROUP_NAME_PROPERTY) String securityGroupName,
      @Named(SecurityGroupModule.CASSANDRA_GOSSIP_PORT_PROPERTY) int listenPort,
      @Named(SecurityGroupModule.SECURITY_GROUP_UPDATE_PERIOD_PROPERTY) int updatePeriod) {
    this.cassandraInstanceService = cassandraInstanceService;
    this.securityGroupService = securityGroupService;
    this.dataCenters = dataCenters;
    this.securityGroupName = securityGroupName;
    this.listenPort = listenPort;
    this.updatePeriod = updatePeriod;
  }

  @Override
  public void startUp() {
    for (String dataCenter : dataCenters) {
      if (!securityGroupService.exists(securityGroupName, dataCenter)) {
        LOG.info("Creating security group {} in data center {}", securityGroupName, dataCenter);
        securityGroupService.create(securityGroupName, dataCenter);
      }
    }
  }

  @Override
  public void runOneIteration() {
    List<CassandraInstance> instances = cassandraInstanceService.findAll();
    for (String dataCenter : dataCenters) {
      Set<String> currentGroupRules = listGroupRules(dataCenter);
      Set<String> requiredGroupRules = requiredRulesFor(instances);
      Set<String> rulesToAdd = Sets.difference(requiredGroupRules, currentGroupRules);
      Set<String> rulesToRemove = Sets.difference(currentGroupRules, requiredGroupRules);
      if (!rulesToAdd.isEmpty()) {
        LOG.info("Adding rules to data center {}: {}", dataCenter, rulesToAdd);
        securityGroupService.authorizeIngressRules(securityGroupName, dataCenter,
            toSecurityGroupPermission(rulesToAdd));
      }
      if (!rulesToRemove.isEmpty()) {
        LOG.info("Removing rules from data center {}: {}", dataCenter, rulesToRemove);
        securityGroupService.revokeIngressRules(securityGroupName, dataCenter,
            toSecurityGroupPermission(rulesToRemove));
      }
    }
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(updatePeriod, updatePeriod, TimeUnit.SECONDS);
  }

  /**
   * Creates ingress rules from IP ranges in CIDR notation with {@code listenPort} as the to-port.
   *
   * @param rules IP ranges in CIDR ("slash") notation
   * @return ingress rules
   */
  private SecurityGroupPermission toSecurityGroupPermission(Collection<String> rules) {
    return new SecurityGroupPermissionImpl(rules, Range.singleton(listenPort));
  }

  /**
   * Retrieve ingress rules tied to the configured security group and {@code dataCenter}.
   *
   * @return set of ip ranges in CIDR notation associated with the security group
   */
  @VisibleForTesting Set<String> listGroupRules(String dataCenter) {
    ImmutableSet.Builder<String> rules = ImmutableSet.builder();
    Set<SecurityGroupPermission> permissions = securityGroupService.getPermissions(securityGroupName, dataCenter);
    for (SecurityGroupPermission permission : permissions) {
      if (permission.getPortRange().contains(listenPort)) {
        rules.addAll(permission.getIpRanges());
      }
    }
    return rules.build();
  }

  /**
   * Build the ingress rules necessary for the given instances.
   *
   * @param instances Cassandra ring instances
   * @return set of ingress rules
   */
  @VisibleForTesting Set<String> requiredRulesFor(List<CassandraInstance> instances) {
    ImmutableSet.Builder<String> permissions = ImmutableSet.builder();
    for (CassandraInstance instance : instances) {
      permissions.add(instance.getPublicIpAddress() + "/32");
    }
    return permissions.build();
  }
}
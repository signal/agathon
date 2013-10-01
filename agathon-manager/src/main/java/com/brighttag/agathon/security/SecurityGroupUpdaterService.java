package com.brighttag.agathon.security;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.ServiceUnavailableException;

/**
 * Periodically updates the security group associated with the Cassandra ring.
 *
 * @author Greg Opaczewski
 * @author codyaray
 * @since 1/11/2013
 */
public class SecurityGroupUpdaterService extends AbstractScheduledService {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityGroupUpdaterService.class);

  private final CassandraRingService cassandraRingService;
  private final SecurityGroupService securityGroupService;
  private final Function<CassandraInstance, String> dataCenterTransformFunction;
  private final int listenPort;
  private final int updatePeriod;
  private final String securityGroupNamePrefix;

  @Inject
  public SecurityGroupUpdaterService(
      CassandraRingService cassandraRingService,
      SecurityGroupService securityGroupService,
      @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)
          Function<CassandraInstance, String> dataCenterTransformFunction,
      @Named(SecurityGroupModule.CASSANDRA_GOSSIP_PORT_PROPERTY) int listenPort,
      @Named(SecurityGroupModule.SECURITY_GROUP_UPDATE_PERIOD_PROPERTY) int updatePeriod,
      @Named(SecurityGroupModule.SECURITY_GROUP_NAME_PREFIX_PROPERTY) String securityGroupNamePrefix) {
    this.cassandraRingService = cassandraRingService;
    this.securityGroupService = securityGroupService;
    this.dataCenterTransformFunction = dataCenterTransformFunction;
    this.listenPort = listenPort;
    this.updatePeriod = updatePeriod;
    this.securityGroupNamePrefix = securityGroupNamePrefix;
  }

  @Override
  public void runOneIteration() {
    try {
      for (CassandraRing ring : cassandraRingService.findAll()) {
        for (String dataCenter : findDataCenters(ring)) {
          String securityGroupName = securityGroupForRing(ring);
          ensureSecurityGroupExists(securityGroupName, dataCenter);
          updateSecurityGroupRules(securityGroupName, dataCenter, ring.getInstances());
        }
      }
    } catch (ServiceUnavailableException e) {
      LOG.warn("Unable to update the Cassandra security groups", e);
    }
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(0, updatePeriod, TimeUnit.SECONDS);
  }

  /**
   * Creates ingress rules from IP ranges in CIDR notation with {@code listenPort} as the to-port.
   *
   * @param rules IP ranges in CIDR ("slash") notation
   * @return ingress rules
   */
  private SecurityGroupPermission toSecurityGroupPermission(Collection<Netmask> rules) {
    return new SecurityGroupPermission(rules, Range.singleton(listenPort));
  }

  /**
   * Retrieve ingress rules tied to the configured security group and {@code dataCenter}.
   *
   * @return set of ip ranges in CIDR notation associated with the security group
   */
  @VisibleForTesting Set<Netmask> listGroupRules(String securityGroupName, String dataCenter) {
    ImmutableSet.Builder<Netmask> rules = ImmutableSet.builder();
    Set<SecurityGroupPermission> permissions = securityGroupService.getPermissions(
        securityGroupName, dataCenter);
    for (SecurityGroupPermission permission : permissions) {
      if (permission.getPortRange().contains(listenPort)) {
        rules.addAll(permission.getNetmasks());
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
  @VisibleForTesting Set<Netmask> requiredRulesFor(Collection<CassandraInstance> instances) {
    ImmutableSet.Builder<Netmask> permissions = ImmutableSet.builder();
    for (CassandraInstance instance : instances) {
      permissions.add(Netmask.fromCidr(instance.getPublicIpAddress() + "/32"));
    }
    return permissions.build();
  }

  private void ensureSecurityGroupExists(String securityGroupName, String dataCenter) {
    if (!securityGroupService.exists(securityGroupName, dataCenter)) {
      LOG.info("Creating security group {} in data center {}", securityGroupName, dataCenter);
      securityGroupService.create(securityGroupName, dataCenter);
    }
  }

  private void updateSecurityGroupRules(String securityGroupName, String dataCenter,
      Set<CassandraInstance> instances) {
    Set<Netmask> currentGroupRules = listGroupRules(securityGroupName, dataCenter);
    Set<Netmask> requiredGroupRules = requiredRulesFor(instances);
    Set<Netmask> rulesToAdd = Sets.difference(requiredGroupRules, currentGroupRules);
    Set<Netmask> rulesToRemove = Sets.difference(currentGroupRules, requiredGroupRules);
    if (!rulesToAdd.isEmpty()) {
      LOG.info("Adding rules to group {} in {}: {}", securityGroupName, dataCenter, rulesToAdd);
      securityGroupService.authorizeIngressRules(securityGroupName, dataCenter,
          toSecurityGroupPermission(rulesToAdd));
    }
    if (!rulesToRemove.isEmpty()) {
      LOG.info("Removing rules from group {} in {}: {}", securityGroupName, dataCenter, rulesToRemove);
      securityGroupService.revokeIngressRules(securityGroupName, dataCenter,
          toSecurityGroupPermission(rulesToRemove));
    }
  }

  private Set<String> findDataCenters(CassandraRing ring) {
    return FluentIterable.from(ring.getInstances())
        .transform(dataCenterTransformFunction)
        .toSet();
  }

  private String securityGroupForRing(CassandraRing ring) {
    return securityGroupNamePrefix + ring.getName();
  }

}

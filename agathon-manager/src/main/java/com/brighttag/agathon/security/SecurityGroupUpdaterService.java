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

import java.util.Collection;
import java.util.Map;
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

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.jmx.ScheduledServiceMBean;
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
public class SecurityGroupUpdaterService extends AbstractScheduledService implements ScheduledServiceMBean {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityGroupUpdaterService.class);

  private final CassandraRingService cassandraRingService;
  private final SecurityGroupService securityGroupService;
  private final Function<CassandraInstance, String> dataCenterTransformFunction;
  private final int listenPort;
  private final int sslListenPort;
  private final Set<String> sslEnabledRings;
  private final Duration updatePeriod;
  private final String securityGroupNamePrefix;

  @Inject
  public SecurityGroupUpdaterService(
      CassandraRingService cassandraRingService,
      SecurityGroupService securityGroupService,
      @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)
          Function<CassandraInstance, String> dataCenterTransformFunction,
      @Named(SecurityGroupModule.CASSANDRA_GOSSIP_PORT_PROPERTY) int listenPort,
      @Named(SecurityGroupModule.CASSANDRA_SSL_GOSSIP_PORT_PROPERTY) int sslListenPort,
      @Named(SecurityGroupModule.CASSANDRA_RING_SSL_ENABLED) Set<String> sslEnabledRings,
      @Named(SecurityGroupModule.SECURITY_GROUP_UPDATE_PERIOD_PROPERTY) Duration updatePeriod,
      @Named(SecurityGroupModule.SECURITY_GROUP_NAME_PREFIX_PROPERTY) String securityGroupNamePrefix) {
    this.cassandraRingService = cassandraRingService;
    this.securityGroupService = securityGroupService;
    this.dataCenterTransformFunction = dataCenterTransformFunction;
    this.listenPort = listenPort;
    this.sslListenPort = sslListenPort;
    this.sslEnabledRings = sslEnabledRings;
    this.updatePeriod = updatePeriod;
    this.securityGroupNamePrefix = securityGroupNamePrefix;
  }

  @Override
  public void runOneIteration() {
    try {
      for (CassandraRing ring : cassandraRingService.findAll()) {
        for (String dataCenter : findDataCenters(ring)) {
          int port = Boolean.TRUE.equals(sslEnabledRings.contains(ring.getName())) ? sslListenPort : listenPort;
          String securityGroupName = securityGroupForRing(ring);
          ensureSecurityGroupExists(securityGroupName, dataCenter);
          updateSecurityGroupRules(securityGroupName, dataCenter, ring.getInstances(), port);
        }
      }
    } catch (ServiceUnavailableException e) {
      LOG.warn("Unable to update the Cassandra security groups", e);
    }
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(0, updatePeriod.getStandardSeconds(), TimeUnit.SECONDS);
  }

  /**
   * Creates ingress rules from IP ranges in CIDR notation with {@code port} as the to-port.
   *
   * @param rules IP ranges in CIDR ("slash") notation
   * @return ingress rules
   */
  private SecurityGroupPermission toSecurityGroupPermission(Collection<Netmask> rules, int port) {
    return new SecurityGroupPermission(rules, Range.singleton(port));
  }

  /**
   * Retrieve ingress rules tied to the configured security group and {@code dataCenter}.
   *
   * @return set of ip ranges in CIDR notation associated with the security group
   */
  @VisibleForTesting Set<Netmask> listGroupRules(String securityGroupName, String dataCenter, int port) {
    ImmutableSet.Builder<Netmask> rules = ImmutableSet.builder();
    Set<SecurityGroupPermission> permissions = securityGroupService.getPermissions(
        securityGroupName, dataCenter);
    for (SecurityGroupPermission permission : permissions) {
      if (permission.getPortRange().contains(port)) {
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
      Netmask netmask = Netmask.fromCidr(instance.getPublicIpAddress() + "/32");
      if (netmask != null) {
        permissions.add(netmask);
      }
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
      Set<CassandraInstance> instances, int port) {
    Set<Netmask> currentGroupRules = listGroupRules(securityGroupName, dataCenter, port);
    Set<Netmask> requiredGroupRules = requiredRulesFor(instances);
    Set<Netmask> rulesToAdd = Sets.difference(requiredGroupRules, currentGroupRules);
    Set<Netmask> rulesToRemove = Sets.difference(currentGroupRules, requiredGroupRules);
    if (!rulesToAdd.isEmpty()) {
      LOG.info("Adding rules for port {} to group {} in {}: {}", port, securityGroupName, dataCenter, rulesToAdd);
      securityGroupService.authorizeIngressRules(securityGroupName, dataCenter,
          toSecurityGroupPermission(rulesToAdd, port));
    } else {
      LOG.debug("No rules to add for port {} to group {} in {}", port, securityGroupName, dataCenter);
    }
    if (!rulesToRemove.isEmpty()) {
      LOG.info("Removing rules for port {} from group {} in {}: {}", port, securityGroupName, dataCenter, rulesToRemove);
      securityGroupService.revokeIngressRules(securityGroupName, dataCenter,
          toSecurityGroupPermission(rulesToRemove, port));
    } else {
      LOG.debug("No rules to remove for port {} from group {} in {}", port, securityGroupName, dataCenter);
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

package com.brighttag.agathon.security;

import com.google.common.collect.ImmutableSet;

/**
 * Service for managing security groups for a data center.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public interface SecurityGroupService {

  /**
   * Check whether a security group exists with the given name in the data center.
   *
   * @param groupName security group name
   * @param dataCenter data center name
   * @return true iff a group with this groupName already exists in this dataCenter
   */
  boolean exists(String groupName, String dataCenter);

  /**
   * Creates a new security group or faults if the group already exists.
   *
   * @param groupName security group name
   * @param dataCenter data center name
   * @throws RuntimeException if this security group already exists
   */
  void create(String groupName, String dataCenter);

  /**
   * Retrieves the set of permissions for a security group.
   *
   * @param groupName security group name
   * @param dataCenter data center name
   * @return set of permissions
   */
  ImmutableSet<SecurityGroupPermission> getPermissions(String groupName, String dataCenter);

  /**
   * Authorizes ingress rules for a security group.
   *
   * @param groupName security group name
   * @param dataCenter data center name
   * @param permission permissions to authorize for security group
   */
  void authorizeIngressRules(String groupName, String dataCenter, SecurityGroupPermission permission);

  /**
   * Revokes ingress rules for a security group.
   *
   * @param groupName security group name
   * @param dataCenter data center name
   * @param permission permissions to revoke from security group
   */
  void revokeIngressRules(String groupName, String dataCenter, SecurityGroupPermission permission);

}

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

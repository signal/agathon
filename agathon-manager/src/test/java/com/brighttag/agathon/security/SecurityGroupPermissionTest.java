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

import com.google.common.collect.Range;
import com.google.common.testing.EqualsTester;

import org.easymock.EasyMockSupport;
import org.junit.Test;

/**
 * @author codyaray
 * @since 9/05/2013
 */
public class SecurityGroupPermissionTest extends EasyMockSupport {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(groupPermission(7000, "1.1.1.1/32"), groupPermission(7000, "1.1.1.1/32"))
      .addEqualityGroup(groupPermission(7000, "2.2.2.2/32"))
      .addEqualityGroup(groupPermission(8888, "1.1.1.1/32"))
      .addEqualityGroup(groupPermission(7000, "1.1.1.1/32", "2.2.2.2/32"))
      .testEquals();
  }

  private SecurityGroupPermission groupPermission(int port, String... ipRanges) {
    return new SecurityGroupPermission(Netmask.fromCidr(Arrays.asList(ipRanges)), Range.singleton(port));
  }

}


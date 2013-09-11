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
    return new SecurityGroupPermission(Netmask.fromCIDR(Arrays.asList(ipRanges)), Range.singleton(port));
  }

}


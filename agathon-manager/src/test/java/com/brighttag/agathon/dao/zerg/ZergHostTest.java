package com.brighttag.agathon.dao.zerg;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

import org.easymock.EasyMockSupport;
import org.junit.Test;

/**
 * @author codyaray
 * @since 9/27/2013
 */
public class ZergHostTest extends EasyMockSupport {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(host("1.1.1.1", "us-east-1a", "1.2.3.4", "domain1", "myrole"),
          host("1.1.1.1", "us-east-1a", "1.2.3.4", "domain1", "myrole"))
      .addEqualityGroup(host("2.2.2.2", "us-east-1a", "1.2.3.4", "domain1", "myrole"))
      .addEqualityGroup(host("1.1.1.1", "eu-west-2c", "1.2.3.4", "domain1", "myrole"))
      .addEqualityGroup(host("1.1.1.1", "us-east-1a", "9.9.9.9", "domain1", "myrole"))
      .addEqualityGroup(host("1.1.1.1", "us-east-1a", "1.2.3.4", "domain2", "myrole"))
      .addEqualityGroup(host("1.1.1.1", "us-east-1a", "1.2.3.4", "domain1", "jellyr"))
      .testEquals();
  }

  private static ZergHost host(String host, String zone, String publicIp, String domain, String... roles) {
    return new ZergHost(host, ImmutableList.copyOf(roles), zone, publicIp, domain);
  }

}

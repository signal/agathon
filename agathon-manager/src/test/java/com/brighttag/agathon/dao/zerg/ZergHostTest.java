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
      .addEqualityGroup(new ZergHost("1.1.1.1", ImmutableList.of("myrole"), "us-east-1a", "1.2.3.4"),
          new ZergHost("1.1.1.1", ImmutableList.of("myrole"), "us-east-1a", "1.2.3.4"))
      .addEqualityGroup(new ZergHost("2.2.2.2", ImmutableList.of("myrole"), "us-east-1a", "1.2.3.4"))
      .addEqualityGroup(new ZergHost("1.1.1.1", ImmutableList.of("jellyr"), "us-east-1a", "1.2.3.4"))
      .addEqualityGroup(new ZergHost("1.1.1.1", ImmutableList.of("myrole"), "eu-west-2c", "1.2.3.4"))
      .addEqualityGroup(new ZergHost("1.1.1.1", ImmutableList.of("myrole"), "us-east-1a", "9.9.9.9"))
      .testEquals();
  }

}

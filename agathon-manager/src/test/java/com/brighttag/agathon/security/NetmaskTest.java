package com.brighttag.agathon.security;

import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 9/27/2013
 */
public class NetmaskTest extends EasyMockSupport {

  @Test
  public void fromCidr() {
    assertEquals(netmask("1.1.1.1", 32), Netmask.fromCidr("1.1.1.1/32"));
  }

  @Test
  public void fromCidr_noRoutingPrefixLength() {
    assertNull(Netmask.fromCidr("1.1.1.1"));
  }

  @Test
  public void fromCidr_tooManySlashes() {
    assertNull(Netmask.fromCidr("1.1.1.1/3/2"));
  }

  @Test
  public void fromCidr_invalidRoutingPrefixLength() {
    assertNull(Netmask.fromCidr("1.1.1.1/abc"));
  }

  @Test
  public void fromCidr_invalidNetworkAddress() {
    assertNull(Netmask.fromCidr("abc/32"));
  }

  @Test
  public void fromCidr_multiple() {
    assertEquals(ImmutableSet.of(netmask("1.1.1.1", 32), netmask("2.2.2.2", 24), netmask("3.3.3.3", 8)),
        Netmask.fromCidr(ImmutableSet.of("1.1.1.1/32", "2.2.2.2/24", "3.3.3.3/8")));
  }

  @Test
  public void fromCidr_multiple_skipsInvalidNetmasks() {
    assertEquals(ImmutableSet.of(netmask("1.1.1.1", 32), netmask("3.3.3.3", 8)),
        Netmask.fromCidr(ImmutableSet.of("1.1.1.1/32", "abc/24", "3.3.3.3/8")));
  }

  @Test
  public void toCidr_multiple() {
    assertEquals(ImmutableSet.of("1.1.1.1/32", "2.2.2.2/24", "3.3.3.3/8"), Netmask.toCidr(
        ImmutableSet.of(netmask("1.1.1.1", 32), netmask("2.2.2.2", 24), netmask("3.3.3.3", 8))));
  }

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(netmask("1.1.1.1", 32), netmask("1.1.1.1", 32))
      .addEqualityGroup(netmask("1.1.1.1", 24))
      .addEqualityGroup(netmask("2.2.2.2", 32))
      .testEquals();
  }

  private Netmask netmask(String networkAddress, int routingPrefixLength) {
    return new Netmask(networkAddress, routingPrefixLength);
  }

}

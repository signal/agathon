package com.brighttag.agathon.service.impl;

import java.math.BigInteger;

import com.google.common.collect.ImmutableList;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.service.TokenService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 6/5/12
 */
public class CompositeTokenServiceTest extends EasyMockSupport {

  private static final BigInteger TOKEN = BigInteger.TEN;

  private TokenService service1;
  private TokenService service2;
  private TokenService service3;
  private Iterable<TokenService> services;

  private TokenService service;

  @Before
  public void setUp() {
    service1 = createMock(TokenService.class);
    service2 = createMock(TokenService.class);
    service3 = createMock(TokenService.class);
    services = ImmutableList.of(service1, service2, service3);
    service = new CompositeTokenService(services);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getToken_first() {
    expect(service1.getToken()).andReturn(TOKEN);
    replayAll();

    assertEquals(TOKEN, service.getToken());
  }

  @Test
  public void getToken_middle() {
    expect(service1.getToken()).andReturn(null);
    expect(service2.getToken()).andReturn(TOKEN);
    replayAll();

    assertEquals(TOKEN, service.getToken());
  }

  @Test
  public void getToken_last() {
    expect(service1.getToken()).andReturn(null);
    expect(service2.getToken()).andReturn(null);
    expect(service3.getToken()).andReturn(TOKEN);
    replayAll();

    assertEquals(TOKEN, service.getToken());
  }

  @Test
  public void getToken_none() {
    expect(service1.getToken()).andReturn(null);
    expect(service2.getToken()).andReturn(null);
    expect(service3.getToken()).andReturn(null);
    replayAll();

    assertNull(service.getToken());
  }

}

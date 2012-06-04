package com.brighttag.agathon.service.impl;

import java.math.BigInteger;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.TokenService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 6/5/12
 */
public class AssignedTokenServiceTest extends EasyMockSupport {

  private static final BigInteger TOKEN = BigInteger.TEN;

  private CassandraInstance coprocess;

  private TokenService service;

  @Before
  public void setUp() {
    coprocess = createMock(CassandraInstance.class);
    service = new AssignedTokenService(coprocess);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getToken() {
    expect(coprocess.getToken()).andReturn(TOKEN);
    replayAll();

    assertEquals(TOKEN, service.getToken());
  }

}

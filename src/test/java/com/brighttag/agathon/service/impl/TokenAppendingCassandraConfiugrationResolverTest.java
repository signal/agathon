package com.brighttag.agathon.service.impl;

import java.math.BigInteger;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationResolver;
import com.brighttag.agathon.service.TokenService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 8/2/12
 */
public class TokenAppendingCassandraConfiugrationResolverTest extends EasyMockSupport {

  private CassandraConfigurationResolver resolver;
  private TokenService service;

  @Before
  public void setUp() {
    service = createMock(TokenService.class);
    resolver = new TokenAppendingCassandraConfigurationResolver(service);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getConfiguration() {
    expect(service.getToken()).andReturn(BigInteger.TEN);
    replayAll();
    CassandraConfiguration expected = new CassandraConfiguration.Builder()
        .initialToken(BigInteger.TEN)
        .build();
    assertEquals(expected, resolver.getConfiguration(CassandraConfiguration.DEFAULT));
  }

}

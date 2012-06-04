package com.brighttag.agathon.resources;

import java.math.BigInteger;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.service.TokenService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 6/5/12
 */
public class TokenResourceTest extends EasyMockSupport {

  private TokenService service;
  private TokenResource resource;

  @Before
  public void setUp() {
    service = createMock(TokenService.class);
    resource = new TokenResource(service);
  }

  @Test
  public void getToken() {
    final BigInteger expected = BigInteger.TEN;
    expect(service.getToken()).andReturn(expected);
    replayAll();

    assertEquals(expected.toString(), resource.getToken());
  }

  @Test(expected = NullPointerException.class)
  public void getToken_null() {
    expect(service.getToken()).andReturn(null);
    replayAll();

    resource.getToken();
  }

}

package com.brighttag.agathon.token;

import java.math.BigInteger;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 6/5/12
 */
public class AlternatingNetworkTopologyTokenServiceTest extends EasyMockSupport {

  private CassandraInstance coprocess;
  private TokenService service;

  @Before
  public void setUp() {
    coprocess = createMock(CassandraInstance.class);
    service = new AlternatingNetworkTopologyTokenService(4);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getToken() {
    expectTokenFor(1, "us-east-1");
    expectTokenFor(2, "us-west-1");
    expectTokenFor(3, "eu-west-1");
    replayAll();

    assertEquals("1808575600", service.getToken(coprocess).toString());
    assertEquals("42535295865117307932921825929151137186", service.getToken(coprocess).toString());
    assertEquals("85070591730234615865843651858314800976", service.getToken(coprocess).toString());
  }

  private void expectTokenFor(int id, String dataCenter) {
    expect(coprocess.getId()).andReturn(id);
    expect(coprocess.getDataCenter()).andReturn(dataCenter);
  }

  @Test
  public void initialToken() {
    replayAll();
    assertEquals(new BigInteger("0"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 0, 0));
    assertEquals(new BigInteger("1"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 0, 1));
    assertEquals(new BigInteger("2"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 0, 2));
    assertEquals(new BigInteger("42535295865117307932921825928971026432"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 1, 0));
    assertEquals(new BigInteger("42535295865117307932921825928971026433"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 1, 1));
    assertEquals(new BigInteger("42535295865117307932921825928971026434"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 1, 2));
    assertEquals(new BigInteger("85070591730234615865843651857942052864"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 2, 0));
    assertEquals(new BigInteger("85070591730234615865843651857942052865"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 2, 1));
    assertEquals(new BigInteger("85070591730234615865843651857942052866"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 2, 2));
    assertEquals(new BigInteger("127605887595351923798765477786913079296"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 3, 0));
    assertEquals(new BigInteger("127605887595351923798765477786913079297"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 3, 1));
    assertEquals(new BigInteger("127605887595351923798765477786913079298"),
        AlternatingNetworkTopologyTokenService.initialToken(4, 3, 2));
  }

}

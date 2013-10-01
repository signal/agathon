package com.brighttag.agathon.cassandra;

import com.google.common.collect.ImmutableList;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/23/12
 */
public class AgathonConnectorTest extends EasyMockSupport {

  private AgathonConnector connector;

  @Before
  public void setUp() {
    connector = createMockBuilder(AgathonConnector.class)
        .withConstructor("localhost", 8094)
        .addMockedMethod("getDataFromUrl", String.class)
        .createMock();
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getSeeds() throws Exception {
    expect(connector.getDataFromUrl(String.format(AgathonConnector.SEED_URL, "localhost", 8094, "myring")))
        .andReturn("host1,host2,host3");
    replayAll();

    assertEquals(ImmutableList.of("host1", "host2", "host3"), connector.getSeeds("myring"));
  }

}

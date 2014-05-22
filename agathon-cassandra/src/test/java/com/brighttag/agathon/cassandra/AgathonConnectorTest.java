/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

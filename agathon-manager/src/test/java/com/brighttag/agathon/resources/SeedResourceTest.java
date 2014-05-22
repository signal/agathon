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

package com.brighttag.agathon.resources;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.SeedService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/25/12
 */
public class SeedResourceTest extends EasyMockSupport {

  private SeedService service;
  private SeedResource resource;
  private CassandraRing ring;

  @Before
  public void setUp() {
    service = createMock(SeedService.class);
    ring = createMock(CassandraRing.class);
    resource = new SeedResource(service, ring);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getSeeds() {
    expect(service.getSeeds(ring)).andReturn(ImmutableSet.of("host1", "host2", "host3"));
    replayAll();

    assertEquals("host1,host2,host3", resource.getSeeds());
  }

}

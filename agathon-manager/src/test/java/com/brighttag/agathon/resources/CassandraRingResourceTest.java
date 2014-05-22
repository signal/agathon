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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.NotFoundException;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraRingResourceTest extends EasyMockSupport {

  private static final String RING_NAME = "myring";

  private CassandraRingResource resource;
  private CassandraRingService service;
  private CassandraInstanceResourceFactory instanceResourceFactory;
  private SeedResourceFactory seedResourceFactory;

  @Before
  public void setUp() {
    service = createMock(CassandraRingService.class);
    instanceResourceFactory = createMock(CassandraInstanceResourceFactory.class);
    seedResourceFactory = createMock(SeedResourceFactory.class);
    resource = new CassandraRingResource(service, instanceResourceFactory, seedResourceFactory);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() throws Exception {
    CassandraRing ring1 = createMock(CassandraRing.class);
    CassandraRing ring2 = createMock(CassandraRing.class);
    ImmutableSet<CassandraRing> rings = ImmutableSet.of(ring1, ring2);
    expect(service.findAll()).andReturn(rings);
    replayAll();

    assertEquals(rings, resource.findAll());
  }

  @Test
  public void createInstance() {
    CassandraRing ring = createMock(CassandraRing.class);
    service.save(ring);
    expect(ring.getName()).andReturn(RING_NAME);
    replayAll();

    Response response = resource.createRing(ring);
    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(RING_NAME, response.getMetadata().getFirst(HttpHeaders.LOCATION).toString());
  }

  @Test
  public void findByName() throws Exception {
    CassandraRing ring = createMock(CassandraRing.class);
    expect(service.findByName(RING_NAME)).andReturn(ring);
    replayAll();

    assertEquals(ring, resource.findByName(RING_NAME));
  }

  @Test
  public void findByName_notFound() throws Exception {
    expect(service.findByName(RING_NAME)).andReturn(null);
    replayAll();

    try {
      resource.findByName(RING_NAME);
      fail("Expected NotFoundException");
    } catch (NotFoundException e) {
      assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
      assertEquals("No ring found with name: " + RING_NAME, e.getResponse().getEntity());
    }
  }

  @Test
  public void deleteRing() throws Exception {
    CassandraRing ring = createMock(CassandraRing.class);
    expect(service.findByName(RING_NAME)).andReturn(ring);
    service.delete(ring);
    replayAll();

    Response response = resource.deleteRing(RING_NAME);
    assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  public void deleteRing_notFound() throws Exception {
    expect(service.findByName(RING_NAME)).andReturn(null);
    replayAll();

    try {
      resource.deleteRing(RING_NAME);
      fail("Expected NotFoundException");
    } catch (NotFoundException e) {
      assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
      assertEquals("No ring found with name: " + RING_NAME, e.getResponse().getEntity());
    }
  }

}

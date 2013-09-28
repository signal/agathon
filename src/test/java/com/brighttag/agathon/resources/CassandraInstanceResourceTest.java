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

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraInstanceService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceResourceTest extends EasyMockSupport {

  private static final String RING_NAME = "myring";
  private static final int CASSANDRA_ID = 1;

  private CassandraInstanceResource resource;
  private CassandraInstanceService service;
  private CassandraRing ring;

  @Before
  public void setUp() {
    service = createMock(CassandraInstanceService.class);
    ring = createMock(CassandraRing.class);
    resource = new CassandraInstanceResource(service, ring);
    expect(ring.getName()).andReturn(RING_NAME).atLeastOnce();
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() throws Exception {
    CassandraInstance instance1 = createMock(CassandraInstance.class);
    CassandraInstance instance2 = createMock(CassandraInstance.class);
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of(instance1, instance2);
    expect(service.findAll(RING_NAME)).andReturn(instances);
    replayAll();

    assertEquals(instances, resource.findAll());
  }

  @Test
  public void createInstance() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    service.save(RING_NAME, instance);
    expect(instance.getId()).andReturn(CASSANDRA_ID);
    replayAll();

    Response response = resource.createInstance(instance);
    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(String.valueOf(CASSANDRA_ID),
        response.getMetadata().getFirst(HttpHeaders.LOCATION).toString());
  }

  @Test
  public void findById() throws Exception {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(service.findById(RING_NAME, CASSANDRA_ID)).andReturn(instance);
    replayAll();

    assertEquals(instance, resource.findById(CASSANDRA_ID));
  }

  @Test
  public void findById_notFound() throws Exception {
    expect(service.findById(RING_NAME, CASSANDRA_ID)).andReturn(null);
    replayAll();

    try {
      resource.findById(CASSANDRA_ID);
      fail("Expected NotFoundException");
    } catch (NotFoundException e) {
      assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
      assertEquals("No instance found with id: " + CASSANDRA_ID, e.getResponse().getEntity());
    }
  }

  @Test
  public void deleteInstance() throws Exception {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(service.findById(RING_NAME, CASSANDRA_ID)).andReturn(instance);
    service.delete(RING_NAME, instance);
    replayAll();

    Response response = resource.deleteInstance(CASSANDRA_ID);
    assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  public void deleteInstance_notFound() throws Exception {
    expect(service.findById(RING_NAME, CASSANDRA_ID)).andReturn(null);
    replayAll();

    try {
      resource.deleteInstance(CASSANDRA_ID);
      fail("Expected NotFoundException");
    } catch (NotFoundException e) {
      assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
      assertEquals("No instance found with id: " + CASSANDRA_ID, e.getResponse().getEntity());
    }
  }

}

package com.brighttag.agathon.resources;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.NotFoundException;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceResourceTest extends EasyMockSupport {

  private static final String CASSANDRA_ID = "id";

  private CassandraInstanceResource resource;
  private CassandraInstanceService service;

  @Before
  public void setUp() {
    service = createMock(CassandraInstanceService.class);
    resource = new CassandraInstanceResource(service);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraInstance instance1 = createMock(CassandraInstance.class);
    CassandraInstance instance2 = createMock(CassandraInstance.class);
    List<CassandraInstance> instances = ImmutableList.of(instance1, instance2);
    expect(service.findAll()).andReturn(instances);
    replayAll();

    assertEquals(instances, resource.findAll());
  }

  @Test
  public void createInstance() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    service.save(instance);
    expect(instance.getId()).andReturn(CASSANDRA_ID);
    replayAll();

    Response response = resource.createInstance(instance);
    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(CASSANDRA_ID, response.getMetadata().getFirst(HttpHeaders.LOCATION).toString());
  }

  @Test
  public void findById() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(service.findById(CASSANDRA_ID)).andReturn(instance);
    replayAll();

    assertEquals(instance, resource.findById(CASSANDRA_ID));
  }

  @Test
  public void findById_notFound() {
    expect(service.findById(CASSANDRA_ID)).andReturn(null);
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
  public void deleteInstance() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(service.findById(CASSANDRA_ID)).andReturn(instance);
    service.delete(instance);
    replayAll();

    Response response = resource.deleteInstance(CASSANDRA_ID);
    assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  public void deleteInstance_notFound() {
    expect(service.findById(CASSANDRA_ID)).andReturn(null);
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

package com.brighttag.agathon.dao.memory;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class MemoryCassandraInstanceDaoTest extends EasyMockSupport {

  private static final int CASSANDRA_ID = 1;

  private Map<Integer, CassandraInstance> instances;
  private MemoryCassandraInstanceDao dao;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    instances = createMock(Map.class);
    dao = new MemoryCassandraInstanceDao(instances);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraInstance instance1 = instance();
    CassandraInstance instance2 = instance();
    Set<CassandraInstance> expected = ImmutableSet.of(instance1, instance2);
    expect(instances.values()).andStubReturn(expected);
    replayAll();

    assertEquals(expected, dao.findAll());
  }

  @Test
  public void findById() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(instances.get(CASSANDRA_ID)).andStubReturn(instance);
    replayAll();

    assertEquals(instance, dao.findById(CASSANDRA_ID));
  }

  @Test
  public void findById_notFound() {
    expect(instances.get(CASSANDRA_ID)).andStubReturn(null);
    replayAll();

    assertNull(dao.findById(CASSANDRA_ID));
  }

  @Test
  public void save() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(instance.getId()).andReturn(CASSANDRA_ID);
    expect(instances.put(CASSANDRA_ID, instance)).andReturn(null);
    replayAll();

    dao.save(instance);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(instance.getId()).andReturn(CASSANDRA_ID);
    expect(instances.remove(CASSANDRA_ID)).andReturn(null);
    replayAll();

    dao.delete(instance);
  }

  private CassandraInstance instance() {
    return new CassandraInstance.Builder().id(1).build();
  }

}

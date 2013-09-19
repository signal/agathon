package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceServiceImplTest extends EasyMockSupport {

  private static final String RING_NAME = "myring";

  private static final int CASSANDRA_ID = 1;

  private CassandraInstanceDao dao;
  private CassandraInstanceServiceImpl service;

  @Before
  public void setUp() {
    dao = createMock(CassandraInstanceDao.class);
    service = new CassandraInstanceServiceImpl(dao);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraInstance instance1 = createMock(CassandraInstance.class);
    CassandraInstance instance2 = createMock(CassandraInstance.class);
    CassandraInstance instance3 = createMock(CassandraInstance.class);
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of(instance1, instance2, instance3);
    expect(dao.findAll(RING_NAME)).andReturn(instances);
    replayAll();

    assertEquals(instances, service.findAll(RING_NAME));
  }

  @Test
  public void findById() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(dao.findById(RING_NAME, CASSANDRA_ID)).andReturn(instance);
    replayAll();

    assertEquals(instance, service.findById(RING_NAME, CASSANDRA_ID));
  }

  @Test
  public void save() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    dao.save(RING_NAME, instance);
    replayAll();

    service.save(RING_NAME, instance);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    dao.delete(RING_NAME, instance);
    replayAll();

    service.delete(RING_NAME, instance);
  }

}

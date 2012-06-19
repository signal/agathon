package com.brighttag.agathon.service.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceServiceImplTest extends EasyMockSupport {

  private static final int CASSANDRA_ID = 1;

  private CassandraInstanceDAO dao;
  private CassandraInstanceServiceImpl service;

  @Before
  public void setUp() {
    dao = createMock(CassandraInstanceDAO.class);
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
    List<CassandraInstance> instances = ImmutableList.of(instance1, instance2, instance3);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(instances, service.findAll());
  }

  @Test
  public void findById() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(dao.findById(CASSANDRA_ID)).andReturn(instance);
    replayAll();

    assertEquals(instance, service.findById(CASSANDRA_ID));
  }

  @Test
  public void save() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    dao.save(instance);
    replayAll();

    service.save(instance);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    dao.delete(instance);
    replayAll();

    service.delete(instance);
  }

}

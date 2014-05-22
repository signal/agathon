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

package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.ServiceUnavailableException;

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
  public void findAll() throws Exception {
    CassandraInstance instance1 = createMock(CassandraInstance.class);
    CassandraInstance instance2 = createMock(CassandraInstance.class);
    CassandraInstance instance3 = createMock(CassandraInstance.class);
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of(instance1, instance2, instance3);
    expect(dao.findAll(RING_NAME)).andReturn(instances);
    replayAll();

    assertEquals(instances, service.findAll(RING_NAME));
  }

  @Test(expected = ServiceUnavailableException.class)
  public void findAll_backingStoreException() throws Exception {
    expect(dao.findAll(RING_NAME)).andThrow(new BackingStoreException());
    replayAll();

    service.findAll(RING_NAME);
  }

  @Test
  public void findById() throws Exception {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(dao.findById(RING_NAME, CASSANDRA_ID)).andReturn(instance);
    replayAll();

    assertEquals(instance, service.findById(RING_NAME, CASSANDRA_ID));
  }

  @Test(expected = ServiceUnavailableException.class)
  public void findById_backingStoreException() throws Exception {
    expect(dao.findById(RING_NAME, CASSANDRA_ID)).andThrow(new BackingStoreException());
    replayAll();

    service.findById(RING_NAME, CASSANDRA_ID);
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

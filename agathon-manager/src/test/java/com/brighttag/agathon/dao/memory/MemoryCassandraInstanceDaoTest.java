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

package com.brighttag.agathon.dao.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class MemoryCassandraInstanceDaoTest extends EasyMockSupport {

  private static final int CASSANDRA_ID = 1;
  private static final String RING_NAME = "myring";

  private ConcurrentMap<String, Map<Integer, CassandraInstance>> rings;
  private MemoryCassandraInstanceDao dao;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    rings = createMock(ConcurrentMap.class);
    dao = new MemoryCassandraInstanceDao(rings);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraInstance instance1 = instance();
    CassandraInstance instance2 = instance();
    expect(rings.containsKey(RING_NAME)).andStubReturn(true);
    expect(rings.get(RING_NAME)).andStubReturn(ImmutableMap.of(1, instance1, 2, instance2));
    replayAll();

    assertEquals(ImmutableSet.of(instance1, instance2), dao.findAll(RING_NAME));
  }

  @Test
  public void findById() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(rings.containsKey(RING_NAME)).andStubReturn(true);
    expect(rings.get(RING_NAME)).andStubReturn(ImmutableMap.of(CASSANDRA_ID, instance));
    replayAll();

    assertEquals(instance, dao.findById(RING_NAME, CASSANDRA_ID));
  }

  @Test
  public void findById_ringNotFound() {
    expect(rings.containsKey(RING_NAME)).andStubReturn(false);
    expect(rings.get(RING_NAME)).andStubReturn(null);
    replayAll();

    assertNull(dao.findById(RING_NAME, CASSANDRA_ID));
  }

  @Test
  public void findById_instanceNotFound() {
    expect(rings.containsKey(RING_NAME)).andStubReturn(true);
    expect(rings.get(RING_NAME)).andStubReturn(ImmutableMap.<Integer, CassandraInstance>of());
    replayAll();

    assertNull(dao.findById(RING_NAME, CASSANDRA_ID));
  }

  @Test
  public void save_existingRing() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    @SuppressWarnings("unchecked")
    Map<Integer, CassandraInstance> instanceMap = createMock(Map.class);
    expect(instance.getId()).andStubReturn(CASSANDRA_ID);
    expect(rings.containsKey(RING_NAME)).andStubReturn(true);
    expect(rings.get(RING_NAME)).andStubReturn(instanceMap);
    expect(instanceMap.put(CASSANDRA_ID, instance)).andStubReturn(null);
    replayAll();

    dao.save(RING_NAME, instance);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void save_newRing() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    expect(instance.getId()).andReturn(CASSANDRA_ID);
    expect(rings.containsKey(RING_NAME)).andReturn(false);
    Map<Integer, CassandraInstance> instanceMap = createMock(Map.class);
    expect(rings.get(RING_NAME)).andReturn(instanceMap);
    expect(rings.put(eq(RING_NAME), anyObject(Map.class))).andReturn(null);
    expect(instanceMap.put(CASSANDRA_ID, instance)).andReturn(null);
    replayAll();

    dao.save(RING_NAME, instance);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    @SuppressWarnings("unchecked")
    Map<Integer, CassandraInstance> instanceMap = createMock(Map.class);
    expect(instance.getId()).andStubReturn(CASSANDRA_ID);
    expect(rings.containsKey(RING_NAME)).andStubReturn(true);
    expect(rings.get(RING_NAME)).andStubReturn(instanceMap);
    expect(instanceMap.remove(CASSANDRA_ID)).andStubReturn(null);
    replayAll();

    dao.delete(RING_NAME, instance);
  }

  private CassandraInstance instance() {
    return new CassandraInstance.Builder().id(CASSANDRA_ID).build();
  }

}

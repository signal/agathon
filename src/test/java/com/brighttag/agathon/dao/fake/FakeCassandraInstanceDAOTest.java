package com.brighttag.agathon.dao.fake;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

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
public class FakeCassandraInstanceDAOTest extends EasyMockSupport {

  private static final String CASSANDRA_ID = "id";

  private Map<String, CassandraInstance> instances;
  private FakeCassandraInstanceDAO dao;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    instances = createMock(Map.class);
    dao = new FakeCassandraInstanceDAO(instances);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraInstance instance1 = buildInstance("1");
    CassandraInstance instance2 = buildInstance("2");
    List<CassandraInstance> expected = ImmutableList.of(instance1, instance2);
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

  private CassandraInstance buildInstance(String token) {
    return new CassandraInstance.Builder()
        .id("1")
        .token(new BigInteger(token))
        .build();
  }

}

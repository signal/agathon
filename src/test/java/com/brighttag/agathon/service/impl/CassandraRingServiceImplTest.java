package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraRingServiceImplTest extends EasyMockSupport {

  private CassandraRingDao dao;
  private CassandraRingServiceImpl service;

  @Before
  public void setUp() {
    dao = createMock(CassandraRingDao.class);
    service = new CassandraRingServiceImpl(dao);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    CassandraRing ring1 = createMock(CassandraRing.class);
    CassandraRing ring2 = createMock(CassandraRing.class);
    CassandraRing ring3 = createMock(CassandraRing.class);
    ImmutableSet<CassandraRing> rings = ImmutableSet.of(ring1, ring2, ring3);
    expect(dao.findAll()).andReturn(rings);
    replayAll();

    assertEquals(rings, service.findAll());
  }

  @Test
  public void findByName() {
    CassandraRing ring = createMock(CassandraRing.class);
    expect(dao.findByName("myring")).andReturn(ring);
    replayAll();

    assertEquals(ring, service.findByName("myring"));
  }

  @Test
  public void save() {
    CassandraRing ring = createMock(CassandraRing.class);
    dao.save(ring);
    replayAll();

    service.save(ring);
  }

  @Test
  public void delete() {
    CassandraRing ring = createMock(CassandraRing.class);
    dao.delete(ring);
    replayAll();

    service.delete(ring);
  }

}

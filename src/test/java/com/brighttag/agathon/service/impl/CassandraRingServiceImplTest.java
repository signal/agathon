package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.ServiceUnavailableException;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraRingServiceImplTest extends EasyMockSupport {

  private static final String RING_NAME = "myring";

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
  public void findAll() throws Exception {
    CassandraRing ring1 = createMock(CassandraRing.class);
    CassandraRing ring2 = createMock(CassandraRing.class);
    CassandraRing ring3 = createMock(CassandraRing.class);
    ImmutableSet<CassandraRing> rings = ImmutableSet.of(ring1, ring2, ring3);
    expect(dao.findAll()).andReturn(rings);
    replayAll();

    assertEquals(rings, service.findAll());
  }

  @Test(expected = ServiceUnavailableException.class)
  public void findAll_backingStoreException() throws Exception {
    expect(dao.findAll()).andThrow(new BackingStoreException());
    replayAll();

    service.findAll();
  }

  @Test
  public void findByName() throws Exception {
    CassandraRing ring = createMock(CassandraRing.class);
    expect(dao.findByName(RING_NAME)).andReturn(ring);
    replayAll();

    assertEquals(ring, service.findByName(RING_NAME));
  }

  @Test(expected = ServiceUnavailableException.class)
  public void findByName_backingStoreException() throws Exception {
    expect(dao.findByName(RING_NAME)).andThrow(new BackingStoreException());
    replayAll();

    service.findByName(RING_NAME);
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

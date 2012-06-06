package com.brighttag.agathon.service.impl;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CoprocessProvider;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 6/6/12
 */
public class SystemPropertyCoprocessProviderTest extends EasyMockSupport {

  private static final String CASSANDRA_ID = "1";

  private CassandraInstanceDAO dao;
  private CoprocessProvider service;

  @Before
  public void setRequiredSystemProperties() {
    System.setProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY, CASSANDRA_ID);
  }

  @After
  public void clearRequiredSystemProperties() {
    System.clearProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY);
  }

  @Before
  public void setUp() {
    dao = createMock(CassandraInstanceDAO.class);
    service = new SystemPropertyCoprocessProvider(dao);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getCassandraCoprocess() {
    final CassandraInstance coprocess = createMock(CassandraInstance.class);
    expect(dao.findById(CASSANDRA_ID)).andReturn(coprocess);
    replayAll();

    assertEquals(coprocess, service.getCassandraCoprocess());
  }

  @Test
  public void getCassandraCoprocess_idNotSet() {
    System.clearProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY);
    replayAll();

    try {
      service.getCassandraCoprocess();
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Cassandra ID must be set", e.getMessage());
    }
  }

  @Test
  public void getCassandraCoprocess_instanceNotInDb() {
    expect(dao.findById(CASSANDRA_ID)).andReturn(null);
    replayAll();

    try {
      service.getCassandraCoprocess();
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Coprocess instance must be in database", e.getMessage());
    }
  }

}

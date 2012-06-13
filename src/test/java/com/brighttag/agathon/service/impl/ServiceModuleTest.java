package com.brighttag.agathon.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.SeedService;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModuleTest extends EasyMockSupport {

  private static final String CASSANDRA_ID = "id";

  @Before
  public void setRequiredSystemProperties() {
    System.setProperty(ServiceModule.CASSANDRA_ID_PROPERTY, CASSANDRA_ID);
  }

  @After
  public void clearRequiredSystemProperties() {
    System.clearProperty(ServiceModule.CASSANDRA_ID_PROPERTY);
  }

  @Test
  public void bindings() {
    Injector injector = Guice.createInjector(new MockDependenciesModule(), new ServiceModule());
    replayAll();

    assertEquals(CassandraInstanceServiceImpl.class,
        injector.getInstance(CassandraInstanceService.class).getClass());
    assertEquals(PerDataCenterSeedService.class,
        injector.getInstance(SeedService.class).getClass());
    verifyAll();
  }

  /**
   * Guice module that mocks {@link ServiceModule} dependencies.
   *
   * @author codyaray
   * @since 5/12/12
   */
  private class MockDependenciesModule extends AbstractModule {

    private final CassandraInstanceDAO dao;

    MockDependenciesModule() {
      dao = createMock(CassandraInstanceDAO.class);
    }

    @Override
    protected void configure() {
      bind(CassandraInstanceDAO.class).toInstance(dao);
    }

  }

}

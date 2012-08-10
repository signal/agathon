package com.brighttag.agathon.service.impl;

import java.io.File;
import java.math.BigInteger;
import java.util.Set;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.easymock.EasyMockSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationResolver;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CoprocessProvider;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.agathon.service.TokenService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModuleTest extends EasyMockSupport {

  private static final int CASSANDRA_ID = 1;

  private static File yamlFile;

  @BeforeClass
  public static void setRequiredSystemProperties() throws Exception {
    yamlFile = File.createTempFile("cassandra", ".yaml"); // TODO: shouldn't be required for testing
    yamlFile.deleteOnExit();
    System.setProperty(ServiceModule.CASSANDRA_YAML_LOCATION, yamlFile.getAbsolutePath());
    System.setProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY, String.valueOf(CASSANDRA_ID));
  }

  @AfterClass
  public static void clearRequiredSystemProperties() {
    System.clearProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY);
  }

  @Test
  public void bindings() {
    Injector injector = Guice.createInjector(new MockDependenciesModule(), new ServiceModule());
    replayAll();

    assertEquals(ChainedCassandraConfigurationResolver.class,
        injector.getInstance(CassandraConfigurationResolver.class).getClass());
    assertEquals(CassandraInstanceServiceImpl.class,
        injector.getInstance(CassandraInstanceService.class).getClass());
    assertEquals(SystemPropertyCoprocessProvider.class,
        injector.getInstance(CoprocessProvider.class).getClass());
    assertEquals(PerDataCenterSeedService.class,
        injector.getInstance(SeedService.class).getClass());
    assertEquals(CompositeTokenService.class,
        injector.getInstance(TokenService.class).getClass());

    assertNotNull(injector.getInstance(Key.get(CassandraConfiguration.class, Coprocess.class)));
    assertNotNull(injector.getInstance(Key.get(CassandraInstance.class, Coprocess.class)));
    assertNotNull(injector.getInstance(CassandraConfigurationRewriterService.class));
    assertNotNull(injector.getInstance(ServiceRegistry.class));

    Set<Service> services = injector.getInstance(Key.get(new TypeLiteral<Set<Service>>() { }));
    assertEquals(CassandraConfigurationRewriterService.class, services.iterator().next().getClass());
    assertEquals(1, services.size());
    verifyAll();
  }

  /**
   * Guice module that mocks {@link ServiceModule} dependencies.
   *
   * @author codyaray
   * @since 5/12/12
   */
  private class MockDependenciesModule extends AbstractModule {

    private final CassandraInstance coprocess;
    private final CassandraInstanceDAO dao;

    MockDependenciesModule() {
      coprocess = createMock(CassandraInstance.class);
      dao = createMock(CassandraInstanceDAO.class);
      expect(dao.findById(CASSANDRA_ID)).andReturn(coprocess);
      expect(coprocess.getToken()).andReturn(BigInteger.ZERO);
    }

    @Override
    protected void configure() {
      bind(CassandraInstanceDAO.class).toInstance(dao);
    }

  }

}

package com.brighttag.agathon.service.impl;

import java.io.File;
import java.io.OutputStream;
import java.util.Set;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.CommitLogConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CommitLogConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.CompactionConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CompactionConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.HintedHandoffConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.HintedHandoffConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.PerformanceConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.PerformanceConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.RpcConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.RpcConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.SnitchConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.SnitchConfigurationWriter;
import com.brighttag.agathon.service.CassandraConfigurationService;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CoprocessProvider;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.agathon.service.TokenService;
import com.brighttag.testing.ModuleTester;

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
  public void bind() throws Exception {
    new ModuleTester(new ServiceModule())
        .dependsOn(CassandraInstanceDAO.class, createMock(CassandraInstanceDAO.class))
        .exposes(CassandraConfigurationService.class)
        .exposes(CassandraInstanceService.class)
        .exposes(CoprocessProvider.class)
        .exposes(SeedService.class)
        .exposes(TokenService.class)
        .exposesMultibinding(Service.class)
        // TODO: This stuff should NOT be exposed
        .exposes(Key.get(Integer.class, Names.named(ServiceModule.NODES_PER_DATACENTER_PROPERTY)))
        .exposes(Key.get(Integer.class, Names.named(ServiceModule.SEEDS_PER_DATACENTER_PROPERTY)))
        .exposes(Key.get(OutputStream.class, Names.named(ServiceModule.CASSANDRA_YAML_LOCATION)))
        .exposes(Key.get(new TypeLiteral<Provider<OutputStream>>() {}, Names.named(ServiceModule.CASSANDRA_YAML_LOCATION)))
        .exposes(Key.get(new TypeLiteral<Iterable<TokenService>>() {}))
        .exposes(CassandraConfigurationResolver.class)
        .exposes(SystemPropertyCassandraConfigurationResolver.class)
        .exposes(TokenAppendingCassandraConfigurationResolver.class)
        .exposes(CassandraConfigurationRewriterService.class)
        .exposes(CassandraConfigurationServiceImpl.class)
        .exposes(CassandraInstanceServiceImpl.class)
        .exposes(SystemPropertyCoprocessProvider.class)
        .exposes(AlternatingNetworkTopologyTokenService.class)
        .exposes(AssignedTokenService.class)
        .exposes(CompositeTokenService.class)
        .exposes(PerDataCenterSeedService.class)
        .exposes(CassandraConfigurationReader.class)
        .exposes(CassandraConfigurationWriter.class)
        .exposes(CommitLogConfigurationReader.class)
        .exposes(CommitLogConfigurationWriter.class)
        .exposes(CompactionConfigurationReader.class)
        .exposes(CompactionConfigurationWriter.class)
        .exposes(HintedHandoffConfigurationReader.class)
        .exposes(HintedHandoffConfigurationWriter.class)
        .exposes(PerformanceConfigurationReader.class)
        .exposes(PerformanceConfigurationWriter.class)
        .exposes(RpcConfigurationReader.class)
        .exposes(RpcConfigurationWriter.class)
        .exposes(SnitchConfigurationReader.class)
        .exposes(SnitchConfigurationWriter.class)
        .exposesNothingElse()
        .verify();
  }

  @Test
  public void bindings() {
    Injector injector = Guice.createInjector(new MockDependenciesModule(), new ServiceModule());
    replayAll();

    assertEquals(CassandraConfigurationServiceImpl.class,
        injector.getInstance(CassandraConfigurationService.class).getClass());
    assertEquals(CassandraInstanceServiceImpl.class,
        injector.getInstance(CassandraInstanceService.class).getClass());
    assertEquals(SystemPropertyCoprocessProvider.class,
        injector.getInstance(CoprocessProvider.class).getClass());
    assertEquals(PerDataCenterSeedService.class,
        injector.getInstance(SeedService.class).getClass());
    assertEquals(CompositeTokenService.class,
        injector.getInstance(TokenService.class).getClass());

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

package com.brighttag.agathon.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationService;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CoprocessProvider;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.agathon.service.TokenService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the services.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModule extends AbstractModule {

  public static final String CASSANDRA_YAML_LOCATION = "com.brighttag.agathon.cassandra.config.file";
  public static final String NODES_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.nodes.per_datacenter";
  public static final String SEEDS_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.seeds.per_datacenter";

  @Override
  protected void configure() {
    bind(CassandraConfigurationService.class).to(CassandraConfigurationServiceImpl.class).in(Singleton.class);
    bind(CassandraInstanceService.class).to(CassandraInstanceServiceImpl.class).in(Singleton.class);
    bind(CoprocessProvider.class).to(SystemPropertyCoprocessProvider.class).in(Singleton.class);
    bind(SeedService.class).to(PerDataCenterSeedService.class).in(Singleton.class);
    bind(TokenService.class).to(CompositeTokenService.class).in(Singleton.class);
    install(new CassandraConfigurationModule());
    install(new CoprocessModule());
    Multibinder.newSetBinder(binder(), Service.class)
        .addBinding().to(CassandraConfigurationRewriterService.class);
  }

  @Provides @Singleton
  Iterable<TokenService> provideTokenServices(AssignedTokenService assignedToken,
      AlternatingNetworkTopologyTokenService newToken) {
    return ImmutableList.of(assignedToken, newToken);
  }

  @Provides @Singleton @Named(NODES_PER_DATACENTER_PROPERTY)
  int provideNodesPerDataCenter() {
    return Integer.getInteger(NODES_PER_DATACENTER_PROPERTY, 4);
  }

  @Provides @Singleton @Named(SEEDS_PER_DATACENTER_PROPERTY)
  int provideSeedsPerDataCenter() {
    return Integer.getInteger(SEEDS_PER_DATACENTER_PROPERTY, 2);
  }

  /**
   * Private helper module for exposing the {@link CassandraConfigurationRewriterService}
   * and hiding all the coprocess specific details.
   *
   * TODO: separate the coprocess specific stuff into a separate app,
   * to allow deployment of the bulk of this app as a normal web service.
   *
   * @author codyaray
   * @since 8/10/12
   */
  static class CoprocessModule extends PrivateModule {

    @Override
    protected void configure() {
      bind(CassandraConfigurationRewriterService.class);
      expose(CassandraConfigurationRewriterService.class);
    }

    @Provides @Singleton @Coprocess
    CassandraConfiguration provideCoprocessCassandraConfiguration(
        CassandraConfigurationService service, @Coprocess CassandraInstance instance) {
      return service.getConfiguration(instance);
    }

    @Provides @Singleton @Coprocess
    CassandraInstance provideCoprocessCassandraInstance(CoprocessProvider coprocessProvider) {
      return coprocessProvider.getCassandraCoprocess();
    }

  }

 /**
   * Private helper module for exposing a {@link CassandraConfigurationResolver}.
   *
   * TODO: make this a lot more configurable. Possibly introduce a
   * DaoCassandraConfigurationResolver that simply stores the Yaml
   * blob in a centralized database and uses it for configuration.
   *
   * @author codyaray
   * @since 8/6/12
   */
  static class CassandraConfigurationModule extends PrivateModule {

    @Override
    protected void configure() {
      bind(CassandraConfigurationResolver.class)
          .to(ChainedCassandraConfigurationResolver.class).in(Singleton.class);
      expose(CassandraConfigurationResolver.class);
    }

    @Provides @Singleton
    Iterable<CassandraConfigurationResolver> provideConfigurationResolvers(
        StreamBasedCassandraConfigurationResolver streamBasedResolver,
        SystemPropertyCassandraConfigurationResolver systemPropertyResolver,
        TokenAppendingCassandraConfigurationResolver tokenAppendingResolver) {
      return ImmutableList.of(streamBasedResolver, systemPropertyResolver, tokenAppendingResolver);
    }

    @Provides @Named(CASSANDRA_YAML_LOCATION)
    InputStream provideCassandraYamlInputStream(
        @Named(CASSANDRA_YAML_LOCATION) File cassandraYamlFile) throws FileNotFoundException {
      return new FileInputStream(cassandraYamlFile);
    }

    @Provides @Named(CASSANDRA_YAML_LOCATION) @Exposed
    OutputStream provideCassandraYamlOutputStream(
        @Named(CASSANDRA_YAML_LOCATION) File cassandraYamlFile) throws FileNotFoundException {
      return new FileOutputStream(cassandraYamlFile);
    }

    @Provides @Singleton @Named(CASSANDRA_YAML_LOCATION)
    File provideCassandraYamlFile() {
      return new File(checkNotNull(System.getProperty(CASSANDRA_YAML_LOCATION),
          "Cassandra YAML file location not specified"));
    }

  }

}

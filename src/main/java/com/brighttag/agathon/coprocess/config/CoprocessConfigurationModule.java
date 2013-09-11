package com.brighttag.agathon.coprocess.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.collect.ImmutableList;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the {@link CassandraConfigurationService}.
 *
 * TODO: make this a lot more configurable. Possibly introduce a
 * DaoCassandraConfigurationResolver that simply stores the Yaml
 * blob in a centralized database and uses it for configuration.
 *
 * @author codyaray
 * @since 8/6/12
 */
public class CoprocessConfigurationModule extends PrivateModule {

  public static final String CASSANDRA_YAML_LOCATION = "com.brighttag.agathon.cassandra.config.file";

  @Override
  protected void configure() {
    bind(SystemPropertyCassandraConfigurationResolver.class).in(Singleton.class);
    bind(TokenAppendingCassandraConfigurationResolver.class).in(Singleton.class);
    bind(CassandraConfigurationResolver.class)
        .to(ChainedCassandraConfigurationResolver.class).in(Singleton.class);
    bind(CassandraConfigurationService.class)
        .to(CassandraConfigurationServiceImpl.class).in(Singleton.class);
    expose(CassandraConfigurationService.class);
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

  @Provides @Exposed @Named(CASSANDRA_YAML_LOCATION)
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

package com.brighttag.agathon.service.impl;

import com.google.inject.Inject;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationResolver;

/**
 * A {@link CassandraConfigurationResolver} that chains the configurations
 * returned by the {@code resolvers}. If multiple {@code resolvers} set the
 * same parameters on the {@link CassandraConfiguration}, the last one wins.
 *
 * @author codyaray
 * @since 8/6/12
 */
public class ChainedCassandraConfigurationResolver implements CassandraConfigurationResolver {

  private final Iterable<CassandraConfigurationResolver> resolvers;

  @Inject
  public ChainedCassandraConfigurationResolver(Iterable<CassandraConfigurationResolver> resolvers) {
    this.resolvers = resolvers;
  }

  @Override
  public CassandraConfiguration getConfiguration(CassandraConfiguration chainedConfiguration) {
    CassandraConfiguration configuration = chainedConfiguration;
    for (CassandraConfigurationResolver resolver : resolvers) {
      configuration = resolver.getConfiguration(configuration);
    }
    return configuration;
  }

}

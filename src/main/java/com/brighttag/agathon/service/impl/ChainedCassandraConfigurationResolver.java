package com.brighttag.agathon.service.impl;

import com.google.inject.Inject;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

/**
 * A {@link CassandraConfigurationResolver} that chains the configurations
 * returned by the {@code resolvers}. If multiple {@code resolvers} set the
 * same parameters on the {@link CassandraConfiguration}, the last one wins.
 *
 * @author codyaray
 * @since 8/6/12
 */
class ChainedCassandraConfigurationResolver implements CassandraConfigurationResolver {

  private final Iterable<CassandraConfigurationResolver> resolvers;

  @Inject
  public ChainedCassandraConfigurationResolver(Iterable<CassandraConfigurationResolver> resolvers) {
    this.resolvers = resolvers;
  }

  @Override
  public CassandraConfiguration getConfiguration(CassandraInstance instance,
      CassandraConfiguration chainedConfiguration) {
    CassandraConfiguration configuration = chainedConfiguration;
    for (CassandraConfigurationResolver resolver : resolvers) {
      configuration = resolver.getConfiguration(instance, configuration);
    }
    return configuration;
  }

}

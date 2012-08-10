package com.brighttag.agathon.service;

import com.brighttag.agathon.model.config.CassandraConfiguration;

/**
 * Resolves the {@link CassandraConfiguration} for the coprocess {@link CassandraInstance}.
 * Optionally builds on a supplied {@code chainedConfiguration}.
 *
 * @author codyaray
 * @since 8/2/12
 */
public interface CassandraConfigurationResolver {

  /**
   * Resolves the configuration for the coprocess Cassandra instance.
   *
   * @param chainedConfiguration the base configuration on which to build
   * @return the configuration
   */
  CassandraConfiguration getConfiguration(CassandraConfiguration chainedConfiguration);

}

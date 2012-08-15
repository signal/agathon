package com.brighttag.agathon.service.impl;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

/**
 * Resolves the {@link CassandraConfiguration} for the Cassandra instance.
 * Builds on a supplied {@code chainedConfiguration}.
 *
 * @author codyaray
 * @since 8/2/12
 */
interface CassandraConfigurationResolver {

  /**
   * Resolves the configuration for the coprocess Cassandra instance.
   *
   * @param instance the Cassandra instance
   * @param chainedConfiguration the base configuration on which to build
   * @return the configuration
   */
  CassandraConfiguration getConfiguration(CassandraInstance instance,
      CassandraConfiguration chainedConfiguration);

}

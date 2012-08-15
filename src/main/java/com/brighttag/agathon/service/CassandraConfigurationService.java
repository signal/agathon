package com.brighttag.agathon.service;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

/**
 * Service for retrieving the configuration for a Cassandra instance.
 *
 * @author codyaray
 * @since 8/10/12
 */
public interface CassandraConfigurationService {

  /**
   * Retrieve the configuration for the Cassandra instance.
   * @param instance the Cassandra instance
   * @return the Cassandra configuration
   */
  CassandraConfiguration getConfiguration(CassandraInstance instance);

}

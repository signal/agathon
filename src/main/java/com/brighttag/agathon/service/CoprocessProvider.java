package com.brighttag.agathon.service;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * Provides the Cassandra coprocess for this Agathon instance. Alternative implementations
 * may provide different mechanisms for identifying the Cassandra coprocess.
 *
 * @author codyaray
 * @since 6/4/2012
 */
public interface CoprocessProvider {

  /**
   * Return the Cassandra coprocess for this Agathon instance.
   * @return the Cassandra coprocess instance
   */
  CassandraInstance getCassandraCoprocess();

}

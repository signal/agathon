package com.brighttag.agathon.service;

import java.util.Set;

/**
 * Provides the set of hostnames used to seed the Cassandra cluster. Alternative
 * implementations may provide support for different network and ring topologies.
 *
 * @author codyaray
 * @since 5/25/12
 */
public interface SeedService {

  /**
   * Returns the seeds for the Cassandra cluster.
   * @return the seeds for the Cassandra cluster
   */
  Set<String> getSeeds();

}

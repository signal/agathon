package com.brighttag.agathon.service;

import java.util.Set;

/**
 * Provides the hostnames of Cassandra instances as seeds for the
 * Cassandra coprocess. Alternative implementations may provide
 * support for different network and ring topologies.
 *
 * @author codyaray
 * @since 5/25/12
 */
public interface SeedService {

  /**
   * Returns the seeds for the coprocess instance.
   * @return the seeds for the coprocess instance
   */
  Set<String> getSeeds();

}

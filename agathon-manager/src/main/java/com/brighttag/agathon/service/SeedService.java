package com.brighttag.agathon.service;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraRing;

/**
 * Provides the set of hostnames used to seed the Cassandra ring. Alternative
 * implementations may provide support for different network and ring topologies.
 *
 * @author codyaray
 * @since 5/25/12
 */
public interface SeedService {

  /**
   * Returns the seeds for a Cassandra ring.
   *
   * @param ring the Cassandra ring
   * @return the seeds for the Cassandra ring
   */
  ImmutableSet<String> getSeeds(CassandraRing ring);

}

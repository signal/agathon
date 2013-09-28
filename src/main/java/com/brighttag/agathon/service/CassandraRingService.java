package com.brighttag.agathon.service;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraRing;

/**
 * Service for manipulating Cassandra rings.
 *
 * @author codyaray
 * @since 9/17/2013
 */
public interface CassandraRingService {

  /**
   * Returns the set of Cassandra rings.
   *
   * @return set of Cassandra rings
   * @throws ServiceUnavailableException if there was a problem communicating with the backing store.
   */
  ImmutableSet<CassandraRing> findAll();

  /**
   * Returns the Cassandra ring with the given {@code name} or {@code null} if not found.
   *
   * @param name the Cassandra ring name
   * @return the Cassandra ring or {@code null} if not found
   * @throws ServiceUnavailableException if there was a problem communicating with the backing store.
   */
  @Nullable CassandraRing findByName(String name);

  /**
   * Saves the Cassandra {@code ring}.
   *
   * @param ring the Cassandra ring
   */
  void save(CassandraRing ring);

  /**
   * Deletes the Cassandra {@code ring}.
   *
   * @param ring the Cassandra ring
   */
  void delete(CassandraRing ring);

}

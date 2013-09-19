package com.brighttag.agathon.service;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * Service for manipulating Cassandra instances.
 *
 * @author codyaray
 * @since 5/12/2012
 */
public interface CassandraInstanceService {

  /**
   * Returns the set of Cassandra instances in a ring.
   *
   * @param ring name of the Cassandra ring
   * @return set of Cassandra instances in the ring
   */
  ImmutableSet<CassandraInstance> findAll(String ring);

  /**
   * Returns the Cassandra instance with the given {@code id} or {@code null} if not found.
   *
   * @param ring name of the Cassandra ring
   * @param id the Cassandra instance ID
   * @return the Cassandra instance or {@code null} if not found
   */
  @Nullable CassandraInstance findById(String ring, int id);

  /**
   * Saves the Cassandra {@code instance}.
   *
   * @param ring name of the Cassandra ring
   * @param instance the Cassandra instance
   */
  void save(String ring, CassandraInstance instance);

  /**
   * Deletes the Cassandra {@code instance}.
   *
   * @param ring name of the Cassandra ring
   * @param instance the Cassandra instance
   */
  void delete(String ring, CassandraInstance instance);

}

package com.brighttag.agathon.dao;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraRing;

/**
 * DAO for Cassandra Rings.
 *
 * @author codyaray
 * @since 9/16/2013
 */
public interface CassandraRingDao {

  /**
   * Returns the set of Cassandra rings.
   *
   * @return set of Cassandra rings
   * @throws BackingStoreException if there was a problem communicating with the backing store.
   */
  ImmutableSet<CassandraRing> findAll() throws BackingStoreException;

  /**
   * Returns the Cassandra ring with the given {@code name} or {@code null} if not found.
   *
   * @param name the Cassandra ring name
   * @return the Cassandra ring or {@code null} if not found
   * @throws BackingStoreException if there was a problem communicating with the backing store.
   */
  @Nullable CassandraRing findByName(String name) throws BackingStoreException;

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

package com.brighttag.agathon.dao;

import java.util.Set;

import javax.annotation.Nullable;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * DAO for Cassandra Instances.
 *
 * @author codyaray
 * @since 5/12/12
 */
public interface CassandraInstanceDao {

  /**
   * Returns the set of Cassandra instances.
   * @return a list of Cassandra instances
   */
  Set<CassandraInstance> findAll();

  /**
   * Finds the Cassandra instance by ID, otherwise returns {@code null}.
   * @param id the Cassandra instance ID
   * @return the entity or {@code null} if it could not be found
   */
  @Nullable CassandraInstance findById(int id);

  /**
   * Saves the Cassandra instance.
   * @param instance the Cassandra instance
   */
  void save(CassandraInstance instance);

  /**
   * Deletes the Cassandra instance from persistent storage.
   * @param instance the Cassandra instance
   */
  void delete(CassandraInstance instance);

}

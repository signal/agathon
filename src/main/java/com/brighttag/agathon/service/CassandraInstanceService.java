package com.brighttag.agathon.service;

import java.util.List;

import javax.annotation.Nullable;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * Resource for manipulating Cassandra instances.
 *
 * @author codyaray
 * @since 5/12/2012
 */
public interface CassandraInstanceService {

  /**
   * Return the list of Cassandra instances ordered by position on ring, indicated by token.
   * @return the list of Cassandra instances
   */
  List<CassandraInstance> findAll();

  /**
   * Return the Cassandra instance with the given {@code id}, or {@code null} if not found.
   * @param id the Cassandra instance ID
   * @return the Cassandra instance or {@code null} if not found
   */
  @Nullable CassandraInstance findById(String id);

  /**
   * Save the given Cassandra {@code instance}.
   * @param instance the Cassandra instance
   */
  void save(CassandraInstance instance);

  /**
   * Delete the given Cassandra {@code instance}.
   * @param instance the Cassandra instance
   */
  void delete(CassandraInstance instance);

}

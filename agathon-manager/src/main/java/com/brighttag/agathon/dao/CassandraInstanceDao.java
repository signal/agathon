/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.dao;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * DAO for Cassandra Instances.
 *
 * @author codyaray
 * @since 5/12/2012
 */
public interface CassandraInstanceDao {

  /**
   * Returns the set of Cassandra instances in a ring.
   *
   * @param ring name of the Cassandra ring
   * @return set of Cassandra instances in the ring
   * @throws BackingStoreException if there was a problem communicating with the backing store.
   */
  ImmutableSet<CassandraInstance> findAll(String ring) throws BackingStoreException;

  /**
   * Returns the Cassandra instance with the given {@code id} or {@code null} if not found.
   *
   * @param ring name of the Cassandra ring
   * @param id the Cassandra instance ID
   * @return the Cassandra instance or {@code null} if not found
   * @throws BackingStoreException if there was a problem communicating with the backing store.
   */
  @Nullable CassandraInstance findById(String ring, int id) throws BackingStoreException;

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

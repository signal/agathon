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

package com.brighttag.agathon.dao.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;

/**
 * In-memory implementation of {@link CassandraInstanceDao}.
 *
 * @author codyaray
 * @since 5/12/12
 */
class MemoryCassandraInstanceDao implements CassandraInstanceDao {

  private static final Logger LOG = LoggerFactory.getLogger(MemoryCassandraInstanceDao.class);

  private final ConcurrentMap<String, Map<Integer, CassandraInstance>> rings;

  public MemoryCassandraInstanceDao() {
    this(Maps.<String, Map<Integer, CassandraInstance>>newConcurrentMap());
  }

  @VisibleForTesting MemoryCassandraInstanceDao(
      ConcurrentMap<String, Map<Integer, CassandraInstance>> rings) {
    this.rings = rings;
  }

  @Override
  public ImmutableSet<CassandraInstance> findAll(String ring) {
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of();
    if (rings.containsKey(ring)) {
      instances = ImmutableSet.copyOf(rings.get(ring).values());
    }
    LOG.info("Returning instances: {}", instances);
    return instances;
  }

  @Override
  public @Nullable CassandraInstance findById(String ring, int id) {
    CassandraInstance instance = null;
    if (rings.containsKey(ring)) {
      instance = rings.get(ring).get(id);
    }
    LOG.info("Returning instance: {}", instance);
    return instance;
  }

  @Override
  public void save(String ringName, CassandraInstance instance) {
    LOG.info("Saving instance: {}", instance);
    if (!rings.containsKey(ringName)) {
      rings.put(ringName, Maps.<Integer, CassandraInstance>newHashMap());
    }
    rings.get(ringName).put(instance.getId(), instance);
  }

  @Override
  public void delete(String ringName, CassandraInstance instance) {
    LOG.info("Deleting instance: {}", instance);
    if (rings.containsKey(ringName)) {
      rings.get(ringName).remove(instance.getId());
    }
  }

}

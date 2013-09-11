package com.brighttag.agathon.dao.memory;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

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

  private final Map<Integer, CassandraInstance> instances;

  public MemoryCassandraInstanceDao() {
    this(Maps.<Integer, CassandraInstance>newHashMap());
  }

  @VisibleForTesting MemoryCassandraInstanceDao(Map<Integer, CassandraInstance> instances) {
    this.instances = instances;
  }

  @Override
  public List<CassandraInstance> findAll() {
    LOG.info("Returning instances: {}", instances.values());
    return Ordering.natural().immutableSortedCopy(instances.values());
  }

  @Override
  public @Nullable CassandraInstance findById(int id) {
    LOG.info("Returning instance: {}", instances.get(id));
    return instances.get(id);
  }

  @Override
  public void save(CassandraInstance instance) {
    LOG.info("Saving instance: {}", instance);
    instances.put(instance.getId(), instance);
  }

  @Override
  public void delete(CassandraInstance instance) {
    LOG.info("Deleting instance: {}", instance);
    instances.remove(instance.getId());
  }

}

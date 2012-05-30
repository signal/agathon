package com.brighttag.agathon.dao.fake;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;

/**
 * In-memory implementation of {@link CassandraInstanceDAO}.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class FakeCassandraInstanceDAO implements CassandraInstanceDAO {

  private static final Logger LOG = LoggerFactory.getLogger(FakeCassandraInstanceDAO.class);

  private final Map<String, CassandraInstance> instances;

  public FakeCassandraInstanceDAO() {
    this(Maps.<String, CassandraInstance>newHashMap());
  }

  @VisibleForTesting FakeCassandraInstanceDAO(Map<String, CassandraInstance> instances) {
    this.instances = instances;
  }

  @Override
  public List<CassandraInstance> findAll() {
    LOG.info("Returning instances: {}", instances.values());
    return ImmutableList.copyOf(instances.values());
  }

  @Override
  public @Nullable CassandraInstance findById(String id) {
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

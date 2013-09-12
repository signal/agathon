package com.brighttag.agathon.service.impl;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.inject.Inject;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

/**
 * DAO-based proxy implementation of {@link CassandraInstanceService}.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceServiceImpl implements CassandraInstanceService {

  private final CassandraInstanceDao dao;

  @Inject
  public CassandraInstanceServiceImpl(CassandraInstanceDao dao) {
    this.dao = dao;
  }

  @Override
  public Set<CassandraInstance> findAll() {
    return dao.findAll();
  }

  @Override
  public @Nullable CassandraInstance findById(int id) {
    return dao.findById(id);
  }

  @Override
  public void save(CassandraInstance instance) {
    dao.save(instance);
  }

  @Override
  public void delete(CassandraInstance instance) {
    dao.delete(instance);
  }

}

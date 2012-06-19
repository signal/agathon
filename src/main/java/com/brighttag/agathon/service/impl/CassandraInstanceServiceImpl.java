package com.brighttag.agathon.service.impl;

import java.util.List;

import javax.annotation.Nullable;

import com.google.inject.Inject;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

/**
 * DAO-Proxy implementation of {@link CassandraInstanceService}.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceServiceImpl implements CassandraInstanceService {

  private final CassandraInstanceDAO dao;

  @Inject
  public CassandraInstanceServiceImpl(CassandraInstanceDAO dao) {
    this.dao = dao;
  }

  @Override
  public List<CassandraInstance> findAll() {
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

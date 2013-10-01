package com.brighttag.agathon.service.impl;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.ServiceUnavailableException;

/**
 * DAO-based proxy implementation of {@link CassandraInstanceService}.
 *
 * @author codyaray
 * @since 5/12/2012
 */
public class CassandraInstanceServiceImpl implements CassandraInstanceService {

  private final CassandraInstanceDao dao;

  @Inject
  public CassandraInstanceServiceImpl(CassandraInstanceDao dao) {
    this.dao = dao;
  }

  @Override
  public ImmutableSet<CassandraInstance> findAll(String ring) {
    try {
      return dao.findAll(ring);
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public @Nullable CassandraInstance findById(String ring, int id) {
    try {
      return dao.findById(ring, id);
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public void save(String ring, CassandraInstance instance) {
    dao.save(ring, instance);
  }

  @Override
  public void delete(String ring, CassandraInstance instance) {
    dao.delete(ring, instance);
  }

}

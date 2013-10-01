package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.ServiceUnavailableException;

/**
 * DAO-based proxy implementation of {@link CassandraRingService}.
 *
 * @author codyaray
 * @since 9/17/2013
 */
public class CassandraRingServiceImpl implements CassandraRingService {

  private final CassandraRingDao dao;

  @Inject
  public CassandraRingServiceImpl(CassandraRingDao dao) {
    this.dao = dao;
  }

  @Override
  public ImmutableSet<CassandraRing> findAll() {
    try {
      return dao.findAll();
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public CassandraRing findByName(String name) {
    try {
      return dao.findByName(name);
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public void save(CassandraRing ring) {
    dao.save(ring);
  }

  @Override
  public void delete(CassandraRing ring) {
    dao.delete(ring);
  }

}

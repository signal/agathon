package com.brighttag.agathon.dao.memory;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

/**
 * Guice module to wire up fake (in-memory) DAOs.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class MemoryDaoModule extends PrivateModule {

  @Override
  protected void configure() {
    bind(MemoryCassandraRingDao.class).in(Singleton.class);
    bind(MemoryCassandraInstanceDao.class).in(Singleton.class);
    bind(CassandraInstanceDao.class).to(MemoryCassandraInstanceDao.class);
    bind(CassandraRingDao.class).to(MemoryCassandraRingDao.class);
    expose(CassandraInstanceDao.class);
    expose(CassandraRingDao.class);
  }

}

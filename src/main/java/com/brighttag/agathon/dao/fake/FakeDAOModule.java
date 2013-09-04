package com.brighttag.agathon.dao.fake;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

import com.brighttag.agathon.dao.CassandraInstanceDAO;

/**
 * Guice module to wire up fake (in-memory) DAOs.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class FakeDAOModule extends PrivateModule {

  @Override
  protected void configure() {
    bind(FakeCassandraInstanceDAO.class).in(Singleton.class);
    bind(CassandraInstanceDAO.class).to(FakeCassandraInstanceDAO.class);
    expose(CassandraInstanceDAO.class);
  }

}

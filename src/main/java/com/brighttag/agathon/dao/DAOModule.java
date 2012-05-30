package com.brighttag.agathon.dao;

import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.fake.FakeDAOModule;
import com.brighttag.agathon.dao.sdb.SdbDAOModule;

/**
 * Guice module to install the appropriate DAO implementation.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class DAOModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(DAOModule.class);

  public static final String DATABASE_PROPERTY = "com.brighttag.agathon.database";

  @Override
  protected void configure() {
    if ("fake".equals(System.getProperty(DATABASE_PROPERTY, "sdb"))) {
      LOG.info("Using in-memory database");
      install(new FakeDAOModule());
    } else {
      LOG.info("Using SimpleDB");
      install(new SdbDAOModule());
    }
  }

}

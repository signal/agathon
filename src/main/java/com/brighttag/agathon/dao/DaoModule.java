package com.brighttag.agathon.dao;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.memory.MemoryDaoModule;
import com.brighttag.agathon.dao.sdb.SdbDaoModule;

/**
 * Guice module to install the appropriate DAO implementation.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class DaoModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(DaoModule.class);

  @VisibleForTesting public static final String DATABASE_PROPERTY = "com.brighttag.agathon.database";

  @Override
  protected void configure() {
    if ("fake".equals(System.getProperty(DATABASE_PROPERTY, "sdb"))) {
      LOG.info("Using in-memory database");
      install(new MemoryDaoModule());
    } else {
      LOG.info("Using SimpleDB");
      install(new SdbDaoModule());
    }
  }

}

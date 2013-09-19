package com.brighttag.agathon.dao;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.memory.MemoryDaoModule;
import com.brighttag.agathon.dao.sdb.SdbDaoModule;
import com.brighttag.agathon.dao.zerg.ZergDaoModule;

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
    String database = System.getProperty(DATABASE_PROPERTY, "sdb");
    if ("memory".equals(database)) {
      LOG.info("Using in-memory instance database");
      install(new MemoryDaoModule());
    } else if ("zerg".equals(database)) {
      LOG.info("Using Zerg as instance database");
      install(new ZergDaoModule());
    } else {
      LOG.info("Using SimpleDB as instance database");
      install(new SdbDaoModule());
    }
  }

}

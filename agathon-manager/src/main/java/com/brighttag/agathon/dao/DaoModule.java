/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

package com.brighttag.agathon.dao.zerg;

import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class ZergDaoModuleTest {

  @Test
  public void bindings() throws Exception {
    System.setProperty(ZergDaoModule.ZERG_REGION_PROPERTY, "us-east-1");
    System.setProperty(ZergDaoModule.ZERG_RING_SCOPES_PROPERTY, "somefile.json");
    new ModuleTester(new ZergDaoModule())
        .exposes(CassandraRingDao.class)
        .exposes(CassandraInstanceDao.class)
        .exposesNothingElse()
        .verify();
    System.clearProperty(ZergDaoModule.ZERG_REGION_PROPERTY);
    System.clearProperty(ZergDaoModule.ZERG_RING_SCOPES_PROPERTY);
  }

}

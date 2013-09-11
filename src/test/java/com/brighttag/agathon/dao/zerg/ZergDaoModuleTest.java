package com.brighttag.agathon.dao.zerg;

import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class ZergDaoModuleTest {

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new ZergDaoModule())
        .exposes(CassandraInstanceDao.class)
        .exposesNothingElse()
        .verify();
  }

}

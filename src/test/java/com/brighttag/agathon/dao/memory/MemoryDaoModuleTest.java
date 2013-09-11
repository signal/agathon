package com.brighttag.agathon.dao.memory;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/5/2013
 */
public class MemoryDaoModuleTest extends EasyMockSupport {

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new MemoryDaoModule())
        .exposes(CassandraInstanceDao.class)
        .exposesNothingElse()
        .verify();
  }

}

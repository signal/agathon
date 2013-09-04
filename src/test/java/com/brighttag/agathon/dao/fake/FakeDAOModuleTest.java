package com.brighttag.agathon.dao.fake;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/5/2013
 */
public class FakeDAOModuleTest extends EasyMockSupport {

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new FakeDAOModule())
        .exposes(CassandraInstanceDAO.class)
        .exposesNothingElse()
        .verify();
  }

}

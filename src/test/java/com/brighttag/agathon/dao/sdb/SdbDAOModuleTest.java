package com.brighttag.agathon.dao.sdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.AwsDaoModule;
import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class SdbDAOModuleTest {

  @Before
  public void setUp() {
    System.setProperty(AwsDaoModule.ACCESS_KEY_PROPERTY, "test-key");
    System.setProperty(AwsDaoModule.SECRET_KEY_PROPERTY, "test-secret");
  }

  @After
  public void tearDown() {
    System.clearProperty(AwsDaoModule.ACCESS_KEY_PROPERTY);
    System.clearProperty(AwsDaoModule.SECRET_KEY_PROPERTY);
  }

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new SdbDAOModule())
        .exposes(CassandraInstanceDAO.class)
        .exposesNothingElse()
        .verify();
  }

}

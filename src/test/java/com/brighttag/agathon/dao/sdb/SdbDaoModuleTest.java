package com.brighttag.agathon.dao.sdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.aws.AwsModule;
import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class SdbDaoModuleTest {

  @Before
  public void setUp() {
    System.setProperty(AwsModule.ACCESS_KEY_PROPERTY, "test-key");
    System.setProperty(AwsModule.SECRET_KEY_PROPERTY, "test-secret");
  }

  @After
  public void tearDown() {
    System.clearProperty(AwsModule.ACCESS_KEY_PROPERTY);
    System.clearProperty(AwsModule.SECRET_KEY_PROPERTY);
  }

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new SdbDaoModule())
        .exposes(CassandraInstanceDao.class)
        .exposesNothingElse()
        .verify();
  }

}

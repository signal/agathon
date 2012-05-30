package com.brighttag.agathon.dao.sdb;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;

import static org.junit.Assert.assertNotNull;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class SdbDAOModuleTest {

  @Before
  public void setUp() {
    System.setProperty(SdbDAOModule.ACCESS_KEY_PROPERTY, "test-key");
    System.setProperty(SdbDAOModule.SECRET_KEY_PROPERTY, "test-secret");
  }

  @After
  public void tearDown() {
    System.clearProperty(SdbDAOModule.ACCESS_KEY_PROPERTY);
    System.clearProperty(SdbDAOModule.SECRET_KEY_PROPERTY);
  }

  @Test
  public void bindings() {
    Injector injector = Guice.createInjector(new SdbDAOModule());
    assertNotNull(injector.getInstance(CassandraInstanceDAO.class));
  }

  @Test(expected = ProvisionException.class)
  public void bindings_accessKeyNotSet() {
    System.clearProperty(SdbDAOModule.ACCESS_KEY_PROPERTY);
    Guice.createInjector(new SdbDAOModule()).getInstance(CassandraInstanceDAO.class);
  }

  @Test(expected = ProvisionException.class)
  public void bindings_secretKeyNotSet() {
    System.clearProperty(SdbDAOModule.SECRET_KEY_PROPERTY);
    Guice.createInjector(new SdbDAOModule()).getInstance(CassandraInstanceDAO.class);
  }

}

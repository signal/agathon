package com.brighttag.agathon.servlet;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.brighttag.agathon.dao.DaoModule;
import com.brighttag.agathon.resources.CassandraInstanceResource;
import com.brighttag.agathon.resources.SeedResource;
import com.brighttag.agathon.resources.ValidatingJacksonJsonProvider;

import static org.junit.Assert.assertNotNull;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class GuiceServletConfigTest {

  private Injector injector;

  @BeforeClass
  public static void setRequiredSystemProperties() {
    System.setProperty(DaoModule.DATABASE_PROPERTY, "fake"); // Use in-memory DAO for unit tests
  }

  @AfterClass
  public static void clearRequiredSystemProperties() {
    System.clearProperty(DaoModule.DATABASE_PROPERTY);
  }

  @Before
  public void setUp() {
    injector = new GuiceServletConfig().getInjector();
  }

  @After
  public void tearDown() {
    // Sadly, have to do this
    injector.getInstance(GuiceFilter.class).destroy();
  }

  @Test
  public void bindings() {
    assertNotNull(injector.getInstance(ValidatingJacksonJsonProvider.class));
    assertNotNull(injector.getInstance(CassandraInstanceResource.class));
    assertNotNull(injector.getInstance(SeedResource.class));
  }

}

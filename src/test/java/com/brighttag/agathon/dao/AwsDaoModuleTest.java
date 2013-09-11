package com.brighttag.agathon.dao;

import com.amazonaws.auth.AWSCredentials;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.testing.ModuleTester;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 6/4/12
 */
public class AwsDaoModuleTest {

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
    new ModuleTester(new AwsDaoModule())
        .exposes(AWSCredentials.class)
        .exposesNothingElse()
        .verify();
  }

  @Test
  public void bindings_values() {
    AWSCredentials credentials = injector().getInstance(AWSCredentials.class);
    assertEquals("test-key", credentials.getAWSAccessKeyId());
    assertEquals("test-secret", credentials.getAWSSecretKey());
  }

  @Test(expected = ProvisionException.class)
  public void bindings_accessKeyNotSet() {
    System.clearProperty(AwsDaoModule.ACCESS_KEY_PROPERTY);
    injector().getInstance(AWSCredentials.class);
  }

  @Test(expected = ProvisionException.class)
  public void bindings_secretKeyNotSet() {
    System.clearProperty(AwsDaoModule.SECRET_KEY_PROPERTY);
    injector().getInstance(AWSCredentials.class);
  }

  private Injector injector() {
    return Guice.createInjector(new AwsDaoModule());
  }

}

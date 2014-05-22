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

package com.brighttag.agathon.aws;

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
public class AwsModuleTest {

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
    new ModuleTester(new AwsModule())
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
    System.clearProperty(AwsModule.ACCESS_KEY_PROPERTY);
    injector().getInstance(AWSCredentials.class);
  }

  @Test(expected = ProvisionException.class)
  public void bindings_secretKeyNotSet() {
    System.clearProperty(AwsModule.SECRET_KEY_PROPERTY);
    injector().getInstance(AWSCredentials.class);
  }

  private Injector injector() {
    return Guice.createInjector(new AwsModule());
  }

}

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

package com.brighttag.agathon.servlet;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.brighttag.agathon.dao.DaoModule;
import com.brighttag.agathon.resources.CassandraRingResource;
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
    System.setProperty(DaoModule.DATABASE_PROPERTY, "memory"); // Use in-memory DAO for unit tests
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
    assertNotNull(injector.getInstance(CassandraRingResource.class));
  }

}

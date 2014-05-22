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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import com.brighttag.agathon.dao.DaoModule;
import com.brighttag.agathon.resources.ResourcesModule;
import com.brighttag.agathon.security.SecurityGroupModule;
import com.brighttag.agathon.service.impl.ServiceModule;

/**
 * Guice servlet listener that includes the Agathon bindings.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class GuiceServletConfig extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new ServletModule(),
        new ResourcesModule(),
        new ServiceModule(),
        new SecurityGroupModule(),
        new DaoModule());
  }

}

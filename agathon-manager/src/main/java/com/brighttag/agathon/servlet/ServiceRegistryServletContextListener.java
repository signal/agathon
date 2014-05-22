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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.service.impl.ServiceRegistry;

/**
 * A {@link ServletContextListener} that ties the servlet container's
 * lifecycle to the internal services' lifecycles.
 *
 * @author Eric Lunt
 * @since 10/6/10
 */
public class ServiceRegistryServletContextListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryServletContextListener.class);

  // This will be null until contextInitialized is done
  private ServiceRegistry serviceRegistry;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();
    Injector injector = (Injector) context.getAttribute(Injector.class.getName());
    try {
      LOG.info("Starting application services");
      serviceRegistry = injector.getInstance(ServiceRegistry.class);
      serviceRegistry.startAndWait();
      LOG.info("Started application services");
    } catch (ConfigurationException e) {
      LOG.info("No services bound, so none started");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    if (serviceRegistry != null) {
      LOG.info("Stopping application services");
      serviceRegistry.stopAndWait();
      LOG.info("Stopped application services");
    }
  }

}

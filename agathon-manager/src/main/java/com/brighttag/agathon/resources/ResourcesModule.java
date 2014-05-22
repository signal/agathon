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

package com.brighttag.agathon.resources;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module to wire up the resources.
 *
 * @author codyaray
 * @since 9/17/2013
 */
public class ResourcesModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(CassandraInstanceResourceFactory.class));
    install(new FactoryModuleBuilder().build(SeedResourceFactory.class));
  }

}


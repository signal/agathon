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


package com.brighttag.agathon.app;

import javax.validation.Validation;
import javax.validation.Validator;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Jersey-Guice servlet bindings for Agathon resources.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class AgathonServletModule extends JerseyServletModule {

  @Override
  protected void configureServlets() {
    serve("/*").with(GuiceContainer.class,
        ImmutableMap.of(
            JSONConfiguration.FEATURE_POJO_MAPPING, "true",
            PackagesResourceConfig.PROPERTY_PACKAGES,
                "com.brighttag.agathon.resources,com.fasterxml.jackson.jaxrs.json"));
  }

  @Provides @Singleton
  Validator provideValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

}

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
public class ServletModule extends JerseyServletModule {

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

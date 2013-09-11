package com.brighttag.agathon.coprocess;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import com.brighttag.agathon.coprocess.config.CassandraConfigurationService;
import com.brighttag.agathon.coprocess.config.CoprocessConfigurationModule;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

/**
 * Guice module for wiring up the {@link CassandraConfigurationRewriterService}.
 *
 * TODO: separate the coprocess specific stuff into a separate app,
 * to allow deployment of the bulk of this app as a normal web service.
 *
 * @author codyaray
 * @since 8/10/12
 */
public class CoprocessModule extends AbstractModule {

  public static final String CONFIG_REWRITE_ENABLED_PROPERTY =
      "com.brighttag.agathon.config.rewrite_enabled";

  @Override
  protected void configure() {
    if (Boolean.getBoolean(CONFIG_REWRITE_ENABLED_PROPERTY)) {
      install(new CoprocessConfigurationModule());
      install(new RewriterModule());
      Multibinder.newSetBinder(binder(), Service.class)
          .addBinding().to(CassandraConfigurationRewriterService.class);
    }
  }

  /**
   * Private helper module to expose {@link CassandraConfigurationRewriterService}
   * and hiding all the coprocess specific details.
   *
   * @author codyaray
   * @since 9/05/2013
   */
  static class RewriterModule extends PrivateModule {

    @Override
    protected void configure() {
      bind(SystemPropertyCoprocessProvider.class).in(Singleton.class);
      bind(CoprocessProvider.class).to(SystemPropertyCoprocessProvider.class);
      bind(CassandraConfigurationRewriterService.class);
      expose(CassandraConfigurationRewriterService.class);
    }

    @Provides @Singleton @Coprocess
    CassandraInstance provideCoprocessCassandraInstance(CoprocessProvider coprocessProvider) {
      return coprocessProvider.getCassandraCoprocess();
    }

    @Provides @Singleton @Coprocess
    CassandraConfiguration provideCoprocessCassandraConfiguration(
        CassandraConfigurationService service, @Coprocess CassandraInstance instance) {
      return service.getConfiguration(instance);
    }

  }

}

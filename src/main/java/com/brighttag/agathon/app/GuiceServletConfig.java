package com.brighttag.agathon.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import com.brighttag.agathon.dao.DAOModule;
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
        new AgathonServletModule(),
        new ServiceModule(),
        new DAOModule());
  }

}

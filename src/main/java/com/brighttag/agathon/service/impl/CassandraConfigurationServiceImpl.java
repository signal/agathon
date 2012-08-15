package com.brighttag.agathon.service.impl;

import com.google.inject.Inject;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.service.CassandraConfigurationService;

/**
 * @author codyaray
 * @since 8/10/12
 */
public class CassandraConfigurationServiceImpl implements CassandraConfigurationService {

  private final CassandraConfigurationResolver resolver;

  @Inject
  public CassandraConfigurationServiceImpl(CassandraConfigurationResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public CassandraConfiguration getConfiguration(CassandraInstance instance) {
    return resolver.getConfiguration(instance, CassandraConfiguration.DEFAULT);
  }

}

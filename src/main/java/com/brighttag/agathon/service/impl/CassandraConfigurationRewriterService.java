package com.brighttag.agathon.service.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.Closeables;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationWriter;

import static com.brighttag.agathon.service.impl.ServiceModule.CASSANDRA_YAML_LOCATION;

/**
 * Rewrites the Cassandra configuration file on {@link #startUp}.
 *
 * @author codyaray
 * @since 8/2/12
 */
public class CassandraConfigurationRewriterService extends AbstractIdleService {

  private final CassandraConfiguration configuration;
  private final CassandraConfigurationWriter writer;
  private final Provider<OutputStream> yamlOutputStreamProvider;

  @Inject
  public CassandraConfigurationRewriterService(
      @Coprocess CassandraConfiguration configuration, CassandraConfigurationWriter writer,
      @Named(CASSANDRA_YAML_LOCATION) Provider<OutputStream> yamlOutputStreamProvider) {
    this.configuration = configuration;
    this.writer = writer;
    this.yamlOutputStreamProvider = yamlOutputStreamProvider;
  }

  @Override
  protected void startUp() throws IOException {
    OutputStream out = yamlOutputStreamProvider.get();
    try {
      writer.writeTo(configuration, out);
    } finally {
      Closeables.closeQuietly(out);
    }
  }

  @Override
  protected void shutDown() {
    // Nothing to do
  }

}

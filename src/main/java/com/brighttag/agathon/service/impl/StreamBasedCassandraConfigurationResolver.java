package com.brighttag.agathon.service.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.google.common.io.Closeables;
import com.google.common.io.Closer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationReader;

import static com.brighttag.agathon.service.impl.ServiceModule.CASSANDRA_YAML_LOCATION;

/**
 * Reads the Cassandra configuration from an input stream.
 *
 * @author codyaray
 * @since 8/6/12
 */
class StreamBasedCassandraConfigurationResolver implements CassandraConfigurationResolver {

  private static final Logger LOG = LoggerFactory.getLogger(StreamBasedCassandraConfigurationResolver.class);

  private final CassandraConfigurationReader reader;
  private final Provider<InputStream> yamlInputStreamProvider;

  @Inject
  public StreamBasedCassandraConfigurationResolver(CassandraConfigurationReader reader,
      @Named(CASSANDRA_YAML_LOCATION) Provider<InputStream> yamlInputStreamProvider) {
    this.reader = reader;
    this.yamlInputStreamProvider = yamlInputStreamProvider;
  }

  @Override
  public CassandraConfiguration getConfiguration(CassandraInstance instance,
      CassandraConfiguration chainedConfiguration) {
    Closer closer = Closer.create();
    InputStream in = closer.register(yamlInputStreamProvider.get());
    try {
      return reader.readFrom(in);
    } catch (IOException e) {
      LOG.warn("Exception reading Cassandra config input stream; returning default", e);
      return chainedConfiguration;
    } finally {
      closeQuietly(closer);
    }
  }

  /**
   * Guava's {@link Closeables#closeQuietly(Closeable)} was deprecated, but it was
   * okay and useful for readable streams where exceptions on close are meaningless.
   */
  private static void closeQuietly(@Nullable Closer closer) {
    try {
      closer.close();
    } catch (IOException e) {
      LOG.error("IOException should not have been thrown.", e);
    }
  }

}

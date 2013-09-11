package com.brighttag.agathon.coprocess;

import com.google.inject.Inject;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link CoprocessProvider} that reads the ID of the Cassandra coprocess from a system
 * property and returns the instance with this id from the {@link CassandraInstanceDao}.
 * <br/>
 * Use of this provider requires the {@code CASSANDRA_ID_PROPERTY} system property
 * to be set and {@link CassandraInstanceService} to contain an instance with this id.
 *
 * @author codyaray
 * @since 6/4/12
 */
public class SystemPropertyCoprocessProvider implements CoprocessProvider {

  public static final String CASSANDRA_ID_PROPERTY = "com.brighttag.agathon.cassandra_id";

  private final CassandraInstanceService service;

  @Inject
  public SystemPropertyCoprocessProvider(CassandraInstanceService service) {
    this.service = service;
  }

  @Override
  public CassandraInstance getCassandraCoprocess() {
    return checkNotNull(service.findById(getCassandraId()), "Coprocess instance must be in database");
  }

  private int getCassandraId() {
    return checkNotNull(Integer.getInteger(CASSANDRA_ID_PROPERTY), "Cassandra ID must be set");
  }

}

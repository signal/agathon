package com.brighttag.agathon.service.impl;

import com.google.inject.Inject;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CoprocessProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link CoprocessProvider} that reads the ID of the Cassandra coprocess from a system
 * property and returns the instance with this id from the {@link CassandraInstanceDAO}.
 * <br/>
 * Use of this provider requires the {@code CASSANDRA_ID_PROPERTY} system property
 * to be set and {@link CassandraInstanceDAO} to contain an instance with this id.
 *
 * @author codyaray
 * @since 6/4/12
 */
public class SystemPropertyCoprocessProvider implements CoprocessProvider {

  public static final String CASSANDRA_ID_PROPERTY = "com.brighttag.agathon.cassandra_id";

  private final CassandraInstanceDAO dao;

  @Inject
  public SystemPropertyCoprocessProvider(CassandraInstanceDAO dao) {
    this.dao = dao;
  }

  @Override
  public CassandraInstance getCassandraCoprocess() {
    return checkNotNull(dao.findById(getCassandraId()), "Coprocess instance must be in database");
  }

  private String getCassandraId() {
    return checkNotNull(System.getProperty(CASSANDRA_ID_PROPERTY), "Cassandra ID must be set");
  }

}

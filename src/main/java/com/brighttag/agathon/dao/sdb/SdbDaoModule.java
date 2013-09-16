package com.brighttag.agathon.dao.sdb;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import com.brighttag.agathon.aws.AwsModule;
import com.brighttag.agathon.dao.CassandraInstanceDao;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the SimpleDB DAO.
 * <br/>
 * Use of this module requires the {@code ACCESS_KEY_PROPERTY}
 * and {@code SECRET_KEY_PROPERTY} system properties to be set.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class SdbDaoModule extends PrivateModule {

  public static final String DOMAIN_PROPERTY = "com.brighttag.agathon.database.sdb.domain_name";

  @Override
  protected void configure() {
    install(new AwsModule());
    bindConstant().annotatedWith(Names.named(DOMAIN_PROPERTY))
        .to(checkNotNull(System.getProperty(DOMAIN_PROPERTY), "SimpleDB domain name must be set"));
    bind(CassandraInstanceDao.class).to(SdbCassandraInstanceDao.class);
    expose(CassandraInstanceDao.class);
  }

  @Provides
  AmazonSimpleDBClient provideAmazonSimpleDBClient(AWSCredentials credentials) {
    return new AmazonSimpleDBClient(credentials);
  }
}

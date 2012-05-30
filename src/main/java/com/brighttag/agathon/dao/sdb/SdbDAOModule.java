package com.brighttag.agathon.dao.sdb;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.dao.CassandraInstanceDAO;

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
public class SdbDAOModule extends PrivateModule {

  public static final String ACCESS_KEY_PROPERTY = "com.brighttag.agathon.aws.access_key";
  public static final String SECRET_KEY_PROPERTY = "com.brighttag.agathon.aws.secret_key";

  @Override
  protected void configure() {
    bind(CassandraInstanceDAO.class).to(SdbCassandraInstanceDAO.class);
    expose(CassandraInstanceDAO.class);
  }

  @Provides
  AmazonSimpleDBClient provideAmazonSimpleDBClient(BasicAWSCredentials credentials) {
    return new AmazonSimpleDBClient(credentials);
  }

  @Provides @Singleton
  BasicAWSCredentials provideBasicAWSCredentials(
      @Named(ACCESS_KEY_PROPERTY) String accessKey,
      @Named(SECRET_KEY_PROPERTY) String secretKey) {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  @Provides @Singleton @Named(ACCESS_KEY_PROPERTY)
  String provideAccessKey() {
    return checkNotNull(System.getProperty(ACCESS_KEY_PROPERTY), "Access Key must be set");
  }

  @Provides @Singleton @Named(SECRET_KEY_PROPERTY)
  String provideSecretKey() {
    return checkNotNull(System.getProperty(SECRET_KEY_PROPERTY), "Secret Key must be set");
  }

}

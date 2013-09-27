package com.brighttag.agathon.dao.zerg;

import com.google.gson.Gson;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

/**
 * Guice module to wire up the Zerg DAO.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergDaoModule extends PrivateModule {

  static final String ZERG_MANIFEST_URL_PROPERTY =
      "com.brighttag.agathon.dao.zerg.manifest_url";
  static final String ZERG_MANIFEST_URL_DEFAULT =
      "http://localhost:9374/manifest/environment/prod/";

  @Override
  protected void configure() {
    bind(Gson.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named(ZERG_MANIFEST_URL_PROPERTY))
        .to(System.getProperty(ZERG_MANIFEST_URL_PROPERTY, ZERG_MANIFEST_URL_DEFAULT));
    bind(ZergConnector.class).to(ZergConnectorImpl.class).in(Singleton.class);
    bind(CassandraRingDao.class).to(ZergCassandraRingDao.class).in(Singleton.class);
    bind(CassandraInstanceDao.class).to(ZergCassandraInstanceDao.class).in(Singleton.class);
    expose(CassandraRingDao.class);
    expose(CassandraInstanceDao.class);
  }

  @Provides @Singleton
  AsyncHttpClient provideAsyncHttpClient(AsyncHttpClientConfig config) {
    return new AsyncHttpClient(config);
  }

  @Provides @Singleton
  AsyncHttpClientConfig provideAsyncHttpClientConfig() {
    return new AsyncHttpClientConfig.Builder()
        .setAllowPoolingConnection(true)
        // Yes, Zerg is THIS SLOW when the manifest isn't cached, especially in AWS.
        .setConnectionTimeoutInMs(10000)
        .setRequestTimeoutInMs(10000)
        .setFollowRedirects(true)
        .setMaximumNumberOfRedirects(3)
        .setMaxRequestRetry(1)
        .build();
  }

}

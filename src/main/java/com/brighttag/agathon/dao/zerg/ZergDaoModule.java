package com.brighttag.agathon.dao.zerg;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.dao.zerg.ZergCassandraInstanceDao.Host;

/**
 * Guice module to wire up the Zerg DAO.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergDaoModule extends PrivateModule {

  // Configuration options
  static final String ZERG_MANIFEST_URL_PROPERTY =
      "com.brighttag.agathon.dao.zerg.manifest_url";

  // Internal bindings and constants
  static final String ZERG_MANIFEST_URL_DEFAULT =
      "http://localhost:9374/manifest/environment/prod/";
  static final String RINGS_PROPERTY = "com.brighttag.agathon.cassandra.rings";

  @Override
  protected void configure() {
    bind(Gson.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named(ZERG_MANIFEST_URL_PROPERTY))
        .to(System.getProperty(ZERG_MANIFEST_URL_PROPERTY, ZERG_MANIFEST_URL_DEFAULT));
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
        .setConnectionTimeoutInMs(5000)
        .setRequestTimeoutInMs(5000)
        .setFollowRedirects(true)
        .setMaximumNumberOfRedirects(3)
        .setMaxRequestRetry(1)
        .build();
  }

  private static Predicate<String> startsWith(final String prefix) {
    return new Predicate<String>() {
      @Override
      public boolean apply(@Nullable String input) {
        return input != null && input.startsWith(prefix);
      }
    };
  }

  @Provides @Named(RINGS_PROPERTY)
  Set<String> provideRings(ZergCassandraInstanceDao dao) {
    Set<String> rings = Sets.newHashSet();
    for (Map.Entry<String, Map<String, Host>> regionEntry : dao.getRegions().entrySet()) {
      for (Map.Entry<String, Host> hostEntry : regionEntry.getValue().entrySet()) {
        rings.addAll(Collections2.filter(hostEntry.getValue().roles, startsWith("cassandra_")));
      }
    }
    return FluentIterable.from(rings)
        .transform(new Function<String, String>() {
          @Override
          public String apply(String role) {
            return role.substring("cassandra_".length());
          }
        })
        .toSet();
  }

}

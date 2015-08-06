/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.dao.zerg;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the Zerg DAO.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergDaoModule extends PrivateModule {

  private static final Logger log = LoggerFactory.getLogger(ZergDaoModule.class);

  // Configuration options
  static final String ZERG_PREFIX = "com.brighttag.agathon.dao.zerg.";
  static final String ZERG_REGION_PROPERTY = ZERG_PREFIX + "region";
  static final String ZERG_RING_CONFIG_PROPERTY = ZERG_PREFIX + "ring_scope_file";
  static final String ZERG_MANIFEST_URL_PROPERTY = ZERG_PREFIX + "manifest_url";
  static final String ZERG_CONNECTION_TIMEOUT_PROPERTY = ZERG_PREFIX + "connection_timeout";
  static final String ZERG_REQUEST_TIMEOUT_PROPERTY = ZERG_PREFIX + "request_timeout";
  static final String ZERG_CACHE_TIMEOUT_PROPERTY = ZERG_PREFIX + "cache_timeout";

  // Internal bindings
  static final String ZERG_CASSANDRA_RING_SCOPES = "ring_scopes";

  // Default values
  static final String ZERG_MANIFEST_URL_DEFAULT = "http://localhost:9374/manifest/environment/prod/";

  private static final Type RING_CONFIG_FILE_TYPE =
      new TypeLiteral<Map<String, Map<String, String>>>() {}.getType();

  @Override
  protected void configure() {
    bind(Gson.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named(ZERG_MANIFEST_URL_PROPERTY))
        .to(System.getProperty(ZERG_MANIFEST_URL_PROPERTY, ZERG_MANIFEST_URL_DEFAULT));
    bindConstant().annotatedWith(Names.named(ZERG_REGION_PROPERTY))
        .to(checkNotNull(System.getProperty(ZERG_REGION_PROPERTY), "Zerg region not specified"));
    bindConstant().annotatedWith(Names.named(ZERG_RING_CONFIG_PROPERTY))
        .to(checkNotNull(System.getProperty(ZERG_RING_CONFIG_PROPERTY), "Zerg ring config file not specified"));
    // Yes, Zerg is THIS SLOW when the manifest isn't cached, especially in AWS.
    bind(Duration.class).annotatedWith(Names.named(ZERG_REQUEST_TIMEOUT_PROPERTY))
        .toInstance(Duration.standardSeconds(Long.getLong(ZERG_REQUEST_TIMEOUT_PROPERTY, 20)));
    bind(Duration.class).annotatedWith(Names.named(ZERG_CONNECTION_TIMEOUT_PROPERTY))
        .toInstance(Duration.standardSeconds(Long.getLong(ZERG_CONNECTION_TIMEOUT_PROPERTY, 20)));
    bind(Duration.class).annotatedWith(Names.named(ZERG_CACHE_TIMEOUT_PROPERTY))
        .toInstance(Duration.standardMinutes(Long.getLong(ZERG_CACHE_TIMEOUT_PROPERTY, 1)));
    bind(ZergConnector.class).to(ZergConnectorImpl.class).in(Singleton.class);
    bind(CassandraRingDao.class).to(ZergCassandraRingDao.class).in(Singleton.class);
    bind(CassandraInstanceDao.class).to(ZergCassandraInstanceDao.class).in(Singleton.class);
    expose(CassandraRingDao.class);
    expose(CassandraInstanceDao.class);
  }

  @Provides @Singleton
  CacheBuilder<Object, Object> provideZergLoadingCache(
      @Named(ZERG_CACHE_TIMEOUT_PROPERTY) Duration cacheTimeout) {
    return CacheBuilder.newBuilder()
        .refreshAfterWrite(cacheTimeout.getStandardSeconds(), TimeUnit.SECONDS);
  }

  @Provides @Singleton
  AsyncHttpClient provideAsyncHttpClient(AsyncHttpClientConfig config) {
    return new AsyncHttpClient(config);
  }

  @Provides @Singleton
  AsyncHttpClientConfig provideAsyncHttpClientConfig(
      @Named(ZERG_CONNECTION_TIMEOUT_PROPERTY) Duration connectionTimeout,
      @Named(ZERG_REQUEST_TIMEOUT_PROPERTY) Duration requestTimeout) {
    PeriodFormatter formatter = PeriodFormat.getDefault();
    log.info("Using connection timeout {} and request timeout {}",
        formatter.print(connectionTimeout.toPeriod()), formatter.print(requestTimeout.toPeriod()));
    return new AsyncHttpClientConfig.Builder()
        .setAllowPoolingConnection(true)
        .setConnectionTimeoutInMs(Ints.saturatedCast(connectionTimeout.getMillis()))
        .setRequestTimeoutInMs(Ints.saturatedCast(requestTimeout.getMillis()))
        .setFollowRedirects(true)
        .setMaximumNumberOfRedirects(3)
        .setMaxRequestRetry(1)
        .build();
  }

  @Provides @Singleton @Named(ZERG_RING_CONFIG_PROPERTY)
  Map<String, Map<String, String>> provideCassandraRingConfigMap(
      @Named(ZERG_RING_CONFIG_PROPERTY) String filename, Gson gson) throws IOException {
    String json = Files.toString(new File(filename), Charsets.UTF_8);
    return gson.fromJson(json, RING_CONFIG_FILE_TYPE);
  }

  @Provides @Singleton @Named(ZERG_CASSANDRA_RING_SCOPES)
  Map<String, String> provideCassandraRingScopes(
      @Named(ZERG_RING_CONFIG_PROPERTY) final Map<String, Map<String, String>> ringConfig) {
    return FluentIterable.from(ringConfig.keySet())
        .toMap(new Function<String, String>() {
          @Override
          public String apply(String ring) {
            return ringConfig.get(ring).get("scope");
          }
        });
  }

}

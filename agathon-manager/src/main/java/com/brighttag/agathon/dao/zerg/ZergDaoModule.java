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
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the Zerg DAO.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergDaoModule extends PrivateModule {

  static final String ZERG_REGION_PROPERTY =
      "com.brighttag.agathon.dao.zerg.region";
  static final String ZERG_RING_SCOPES_PROPERTY =
      "com.brighttag.agathon.dao.zerg.ring_scope_file";
  static final String ZERG_MANIFEST_URL_PROPERTY =
      "com.brighttag.agathon.dao.zerg.manifest_url";
  static final String ZERG_MANIFEST_URL_DEFAULT =
      "http://localhost:9374/manifest/environment/prod/";

  @Override
  protected void configure() {
    bind(Gson.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named(ZERG_MANIFEST_URL_PROPERTY))
        .to(System.getProperty(ZERG_MANIFEST_URL_PROPERTY, ZERG_MANIFEST_URL_DEFAULT));
    bindConstant().annotatedWith(Names.named(ZERG_REGION_PROPERTY))
        .to(checkNotNull(System.getProperty(ZERG_REGION_PROPERTY), "Zerg region not specified"));
    bindConstant().annotatedWith(Names.named(ZERG_RING_SCOPES_PROPERTY))
        .to(checkNotNull(System.getProperty(ZERG_RING_SCOPES_PROPERTY), "Zerg ring scopes not specified"));
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
        .setConnectionTimeoutInMs(20000)
        .setRequestTimeoutInMs(20000)
        .setFollowRedirects(true)
        .setMaximumNumberOfRedirects(3)
        .setMaxRequestRetry(1)
        .build();
  }

  @Provides @Singleton @Named(ZERG_RING_SCOPES_PROPERTY)
  Map<String, String> provideCassandraRingScopeMap(
      @Named(ZERG_RING_SCOPES_PROPERTY) String filename, Gson gson) throws IOException {
    String json = Files.toString(new File(filename), Charsets.UTF_8);
    return gson.fromJson(json, Types.newParameterizedType(Map.class, String.class, String.class));
  }

}

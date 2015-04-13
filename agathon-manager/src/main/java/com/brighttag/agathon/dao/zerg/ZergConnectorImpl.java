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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.BackingStoreException;

/**
 * @author codyaray
 * @since 9/27/2013
 */
class ZergConnectorImpl implements ZergConnector {

  private static final Logger LOG = LoggerFactory.getLogger(ZergConnectorImpl.class);

  private final String manifestUrl;
  // HACK: manifestUrl is top-level to load all regions at once. manifestUrl -> region -> hostname -> host.
  private final LoadingCache<String, Map<String, Map<String, ZergHost>>> regionsCache;

  @Inject
  public ZergConnectorImpl(@Named(ZergDaoModule.ZERG_MANIFEST_URL_PROPERTY) String manifestUrl,
      CacheBuilder<Object, Object> regionsCache, ZergConnectorImpl.ZergLoader loader) {
    this.manifestUrl = manifestUrl;
    this.regionsCache = regionsCache.build(loader);
  }

  @Override
  public ImmutableSet<ZergHost> getHosts() throws BackingStoreException {
    ImmutableSet.Builder<ZergHost> hosts = ImmutableSet.builder();
    for (Map.Entry<String, Map<String, ZergHost>> regionEntry : getRegions().entrySet()) {
      Map<String, ZergHost> region = regionEntry.getValue();
      for (Map.Entry<String, ZergHost> hostEntry : region.entrySet()) {
        ZergHost host = hostEntry.getValue();
        host.setName(hostEntry.getKey());
        hosts.add(host);
      }
    }
    return hosts.build();
  }

  private Map<String, Map<String, ZergHost>> getRegions() throws BackingStoreException {
    try {
      return regionsCache.get(manifestUrl);
    } catch (ExecutionException e) {
      Throwables.propagateIfInstanceOf(e.getCause(), BackingStoreException.class);
    }
    // Should be unreachable; ExecutionException should always wrap BackingStoreException
    return ImmutableMap.of();
  }

  public static class ZergLoader extends CacheLoader<String, Map<String, Map<String, ZergHost>>> {

    private final AsyncHttpClient client;
    private final Gson gson;

    @Inject
    public ZergLoader(AsyncHttpClient client, Gson gson) {
      this.client = client;
      this.gson = gson;
    }

    @Override
    public ListenableFuture<Map<String, Map<String, ZergHost>>> reload(@Nonnull String manifestUrl,
        @Nonnull Map<String, Map<String, ZergHost>> oldValue) throws Exception {
      return Futures.transform(execute(manifestUrl), parseResponse(manifestUrl));
    }

    @Override
    public Map<String, Map<String, ZergHost>> load(@Nonnull String manifestUrl)
        throws InterruptedException, BackingStoreException {
      try {
        return Futures.transform(execute(manifestUrl), parseResponse(manifestUrl)).get();
      } catch (ExecutionException e) {
        LOG.warn("Caught exception fetching manifest from zerg {}", manifestUrl, e);
        throw new BackingStoreException(e.getCause());
      }
    }

    private static final TypeLiteral<Map<String, Map<String, ZergHost>>> MAP_OF_REGIONS =
        new TypeLiteral<Map<String, Map<String, ZergHost>>>() { };

    private Function<Response, Map<String, Map<String, ZergHost>>> parseResponse(final String url) {
      return new Function<Response, Map<String, Map<String, ZergHost>>>() {
        @Override
        public Map<String, Map<String, ZergHost>> apply(Response response) {
          try {
            return gson.fromJson(response.getResponseBody(), MAP_OF_REGIONS.getType());
          } catch (IOException e) {
            LOG.warn("Unable to fetch manifest from zerg url: {}", url, e);
            throw new RuntimeException(e);
          } catch (JsonSyntaxException e) {
            LOG.warn("Received bad JSON from Zerg {}: {}", url, e);
            throw new RuntimeException(e);
          }
        }
      };
    }

    private ListenableFuture<Response> execute(String url) throws BackingStoreException {
      try {
        return adapt(client.prepareGet(url).execute());
      } catch (IOException e) {
        LOG.warn("Unable to fetch manifest from zerg url: {}", url, e);
        throw new BackingStoreException(e);
      }
    }
  }

  private static <V> ListenableFuture<V> adapt(com.ning.http.client.ListenableFuture<V> future) {
    return new ListenableFutureAdaptor<V>(future);
  }

  /**
   * Ning's Http Client implements its own ListenableFuture. We use Guava's.
   * This adapter exists to translate between the two types. Sigh...
   *
   * @param <V> the type of the future
   */
  @VisibleForTesting
  public static class ListenableFutureAdaptor<V> extends ForwardingFuture<V>
      implements ListenableFuture<V> {

    private final com.ning.http.client.ListenableFuture<V> future;

    public ListenableFutureAdaptor(com.ning.http.client.ListenableFuture<V> future) {
      this.future = future;
    }

    @Override
    protected Future<V> delegate() {
      return future;
    }

    @Override
    public void addListener(@Nonnull Runnable listener, @Nonnull Executor executor) {
      future.addListener(listener, executor);
    }
  }
}

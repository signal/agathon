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

package com.brighttag.agathon.cassandra;

import java.io.IOException;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a connection to Agathon.
 *
 * @author codyaray
 * @since 5/23/12
 */
public class AgathonConnector {

  private static final Logger LOG = LoggerFactory.getLogger(AgathonConnector.class);

  private static final Splitter SEED_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

  @VisibleForTesting static final String SEED_URL = "http://%s:%s/rings/%s/seeds";

  private final String host;
  private final int port;

  public AgathonConnector(String host, @Nullable Integer port) {
    this.host = host;
    this.port = Objects.firstNonNull(port, 8094);
  }

  /**
   * Read the list of seeds from Agathon.
   *
   * @return the list of seeds from Agathon
   * @throws ConfigurationException if a problem prevented us from reading the seeds from Agathon
   */
  public List<String> getSeeds(String ring) throws ConfigurationException {
    return ImmutableList.copyOf(SEED_SPLITTER.split(
        getDataFromUrl(String.format(SEED_URL, host, port, ring))));
  }

  @VisibleForTesting @Nullable String getDataFromUrl(String url) throws ConfigurationException {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      if (!isSuccess(connection.getResponseCode())) {
        throw configurationException(url);
      }
      String data = readFrom(connection);
      LOG.info("Calling Agathon API {} returns {}", url, data);
      InetAddress localHost = InetAddress.getLocalHost();
      String localHostIp = localHost.getHostAddress();
      LOG.info("Local ip is: {} ignoring if in seed list", localHostIp);
      connection.disconnect();
      return data;
    } catch (IOException e) {
      throw configurationException(url, e);
    }
  }

  private static String readFrom(URLConnection connection) throws IOException {
    return CharStreams.toString(new InputStreamReader(connection.getInputStream(), "UTF-8"));
  }

  private static boolean isSuccess(int statusCode) {
    return statusCode >= 200 && statusCode < 300;
  }

  private static ConfigurationException configurationException(String url) {
    return configurationException(url, null);
  }

  private static ConfigurationException configurationException(String url, @Nullable Exception e) {
    String message = "Unable to get configuration data from Agathon: " + url;
    LOG.info(message, e);
    return new ConfigurationException(message, e);
  }

}

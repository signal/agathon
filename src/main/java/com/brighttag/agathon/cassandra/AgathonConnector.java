package com.brighttag.agathon.cassandra;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import org.apache.cassandra.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a connection to the Agathon coprocess.
 *
 * @author codyaray
 * @since 5/23/12
 */
public class AgathonConnector {

  private static final Logger LOG = LoggerFactory.getLogger(AgathonConnector.class);

  private static final Splitter SEED_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

  @VisibleForTesting static final String SEED_URL =
      System.getProperty("com.brighttag.agathon.seeds_url", "http://127.0.0.1:8080/agathon/seeds");

  /**
   * Read the list of seeds from Agathon.
   *
   * @return the list of seeds from Agathon
   * @throws ConfigurationException if a problem prevented us from reading the seeds from Agathon
   */
  public List<String> getSeeds() throws ConfigurationException {
    return ImmutableList.copyOf(SEED_SPLITTER.split(getDataFromUrl(SEED_URL)));
  }

  @VisibleForTesting @Nullable String getDataFromUrl(String url) throws ConfigurationException {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      if (!isSuccess(connection.getResponseCode())) {
        throw configurationException(url);
      }
      String data = readFrom(connection);
      LOG.info("Calling Agathon API {} returns {}", url, data);
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

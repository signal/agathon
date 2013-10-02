package com.brighttag.agathon.cassandra;

/**
 * Represents a problem with reading the configuration from the Agathon Manager.
 *
 * @author codyaray
 * @since 10/2/2013
 */
public class ConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConfigurationException() {
  }

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(Throwable cause) {
    super(cause);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

}

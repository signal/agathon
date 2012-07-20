package com.brighttag.yaml;

/**
 * Base exception class for Yaml processing.
 *
 * @author codyaray
 * @since 5/19/12
 */
public class YamlException extends HeterogeneousException {

  private static final long serialVersionUID = 1L;

  public YamlException(String message) {
    super(message);
  }

  public YamlException(Throwable cause) {
    super(cause);
  }

  public YamlException(String message, Throwable cause) {
    super(message, cause);
  }

}

package com.brighttag.yaml;

/**
 * Base exception class for heterogeneous processing.
 *
 * @author codyaray
 * @since 5/19/12
 */
public class HeterogeneousException extends Exception {

  private static final long serialVersionUID = 1L;

  public HeterogeneousException(String message) {
    super(message);
  }

  public HeterogeneousException(Throwable cause) {
    super(cause);
  }

  public HeterogeneousException(String message, Throwable cause) {
    super(message, cause);
  }

}

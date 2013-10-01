package com.brighttag.agathon.service;

/**
 * Represents an error due to an unavailable or misbehaving downstream service.
 *
 * @author codyaray
 * @since 9/27/2013
 */
public class ServiceUnavailableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ServiceUnavailableException() {
  }

  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(Throwable cause) {
    super(cause);
  }

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }

}

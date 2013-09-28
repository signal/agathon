package com.brighttag.agathon.dao;

/**
 * Represents an error communicating with the backing store. This could be due
 * to a connection timeout, a thread interruption, or an issue parsing the response.
 *
 * @author codyaray
 * @since 9/27/2013
 */
public class BackingStoreException extends Exception {
  private static final long serialVersionUID = 1L;

  public BackingStoreException() {
  }

  public BackingStoreException(String message) {
    super(message);
  }

  public BackingStoreException(Throwable cause) {
    super(cause);
  }

  public BackingStoreException(String message, Throwable cause) {
    super(message, cause);
  }

}

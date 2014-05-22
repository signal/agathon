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

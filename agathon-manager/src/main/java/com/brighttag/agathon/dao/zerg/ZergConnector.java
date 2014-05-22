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

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.dao.BackingStoreException;

/**
 * Connector for retrieving the manifest from Zerg.
 *
 * @author codyaray
 * @since 9/27/2013
 */
interface ZergConnector {

  /**
   * Retrieves the set of hosts from Zerg.
   * @return the set of hosts from Zerg
   * @throws BackingStoreException if there was a problem communicating with the backing store.
   */
  ImmutableSet<ZergHost> getHosts() throws BackingStoreException;

}

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

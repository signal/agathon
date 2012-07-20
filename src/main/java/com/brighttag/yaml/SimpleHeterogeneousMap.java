package com.brighttag.yaml;

import java.util.Map;

/**
 * A simple heterogeneous map backed by a map with unknown key and value types.
 *
 * @author codyaray
 * @since 7/19/12
 */
class SimpleHeterogeneousMap extends AbstractHeterogeneousMap {

  SimpleHeterogeneousMap(Map<?, ?> object) {
    super(object);
  }

}

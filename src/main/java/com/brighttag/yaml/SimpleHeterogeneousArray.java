package com.brighttag.yaml;

import java.util.List;

/**
 * A simple heterogeneous array backed by a list with unknown value types.
 *
 * @author codyaray
 * @since 7/19/12
 */
class SimpleHeterogeneousArray extends AbstractHeterogeneousArray {

  public SimpleHeterogeneousArray(List<?> array) {
    super(array);
  }

}

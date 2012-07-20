package com.brighttag.yaml;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

/**
 * Base class for heterogeneous maps.
 *
 * @author codyaray
 * @since 7/18/12
 */
public abstract class AbstractHeterogeneousMap extends AbstractHeterogeneousContainer<String>
    implements HeterogeneousMap {

  private final ImmutableMap<?, ?> object;

  AbstractHeterogeneousMap(@Nullable Map<?, ?> object) {
    this.object = (object == null) ?
        ImmutableMap.of() :
        ImmutableMap.copyOf(object);
  }

  @Override
  public @Nullable Object opt(String key) {
    return key == null ? null : object.get(key);
  }

  @Override
  public boolean has(String key) {
    return object.containsKey(key);
  }

  @Override
  public Map<?, ?> asMap() {
    return object;
  }

  @Override
  public int hashCode() {
    return object.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj == this) {
      return true;
    } else if (!getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    return object.equals(getClass().cast(obj).object);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("object", object)
        .toString();
  }

}

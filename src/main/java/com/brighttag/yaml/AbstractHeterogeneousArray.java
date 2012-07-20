package com.brighttag.yaml;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Base class for heterogeneous arrays.
 *
 * @author codyaray
 * @since 7/19/12
 */
public abstract class AbstractHeterogeneousArray extends AbstractHeterogeneousContainer<Integer>
    implements HeterogeneousArray {

  private final List<?> array;

  public AbstractHeterogeneousArray(@Nullable List<?> array) {
    this.array = (array == null) ?
        ImmutableList.of() :
        ImmutableList.copyOf(array);
  }

  @Override
  public Object opt(Integer index) {
    return (index < 0 || index >= size()) ? null : array.get(index);
  }

  @Override
  public int size() {
    return array.size();
  }

  @Override
  public List<?> asList() {
    return array;
  }

  @Override
  public int hashCode() {
    return array.hashCode();
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
    return array.equals(getClass().cast(obj).array);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("array", array)
        .toString();
  }

}

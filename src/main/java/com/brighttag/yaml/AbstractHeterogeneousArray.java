package com.brighttag.yaml;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Base class for heterogeneous arrays.
 *
 * @author codyaray
 * @since 7/19/12
 */
public abstract class AbstractHeterogeneousArray extends AbstractHeterogeneousContainer<Integer>
    implements HeterogeneousArray {

  private final ImmutableList<Object> array;

  protected AbstractHeterogeneousArray(Builder<?> builder) {
    this(builder.array);
  }

  protected AbstractHeterogeneousArray(List<Object> array) {
    this.array = ImmutableList.copyOf(array);
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
  public List<Object> asList() {
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

  /**
   * @author codyaray
   * @since 7/24/12
   */
  public static class Builder<B extends Builder<?>> {

    private final Class<B> clazz;
    private final List<Object> array;

    public Builder(Class<B> klass) {
      this.clazz = klass;
      this.array = Lists.newArrayList();
    }

    public B add(boolean value) {
      array.add(value);
      return clazz.cast(this);
    }

    public B add(int value) {
      array.add(value);
      return clazz.cast(this);
    }

    public B add(long value) {
      array.add(value);
      return clazz.cast(this);
    }

    public B add(double value) {
      array.add(value);
      return clazz.cast(this);
    }

    public B add(String value) {
      array.add(value);
      return clazz.cast(this);
    }

    public B add(YamlArray value) {
      array.add(value.asList());
      return clazz.cast(this);
    }

    public B add(YamlObject value) {
      array.add(value.asMap());
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable Boolean value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable Integer value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable Long value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable Double value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable String value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable YamlArray value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(String key, @Nullable YamlObject value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public B addIfNotNull(YamlObject value) {
      if (value != null) {
        return add(value);
      }
      return clazz.cast(this);
    }

    public <T> B add(Optional<T> value) {
      if (value.isPresent()) {
        array.add(value.get());
      }
      return clazz.cast(this);
    }

    public <T> B addIfNotNull(String key, Optional<T> value) {
      return add(value);
    }

  }

}

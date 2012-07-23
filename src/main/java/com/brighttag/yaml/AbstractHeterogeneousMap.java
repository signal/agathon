package com.brighttag.yaml;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Base class for heterogeneous maps.
 *
 * @author codyaray
 * @since 7/18/12
 */
public abstract class AbstractHeterogeneousMap extends AbstractHeterogeneousContainer<String>
    implements HeterogeneousMap {

  private final ImmutableMap<String, Object> object;

  protected AbstractHeterogeneousMap(Map<String, Object> object) {
    this.object = ImmutableMap.copyOf(object);
  }

  protected AbstractHeterogeneousMap(Builder<?> builder) {
    this(builder.object);
  }

  @Override
  public @Nullable Object opt(@Nullable String key) {
    return key == null ? null : object.get(key);
  }

  @Override
  public boolean has(String key) {
    return object.containsKey(key);
  }

  @Override
  public Map<String, Object> asMap() {
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

  /**
   * @author codyaray
   * @since 7/22/12
   */
  public static class Builder<B extends Builder<?>> {

    private final Class<B> clazz;
    private final Map<String, Object> object;

    public Builder(Class<B> klass) {
      this.clazz = klass;
      this.object = Maps.newLinkedHashMap();
    }

    public B put(String key, boolean value) {
      object.put(key, value);
      return clazz.cast(this);
    }

    public B put(String key, int value) {
      object.put(key, value);
      return clazz.cast(this);
    }

    public B put(String key, long value) {
      object.put(key, value);
      return clazz.cast(this);
    }

    public B put(String key, double value) {
      object.put(key, value);
      return clazz.cast(this);
    }

    public B put(String key, String value) {
      object.put(key, value);
      return clazz.cast(this);
    }

    public B put(String key, YamlArray value) {
      object.put(key, value.asList());
      return clazz.cast(this);
    }

    public B put(String key, YamlObject value) {
      object.put(key, value.asMap());
      return clazz.cast(this);
    }

    public B put(YamlObject value) {
      object.putAll(value.asMap());
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable Boolean value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable Integer value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable Long value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable Double value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable String value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable YamlArray value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(String key, @Nullable YamlObject value) {
      if (value != null) {
        return put(key, value);
      }
      return clazz.cast(this);
    }

    public B putIfNotNull(YamlObject value) {
      if (value != null) {
        return put(value);
      }
      return clazz.cast(this);
    }

    public <T> B put(String key, Optional<T> value) {
      if (value.isPresent()) {
        object.put(key, value.get());
      }
      return clazz.cast(this);
    }

    public <T> B putIfNotNull(String key, Optional<T> value) {
      return put(key, value);
    }

    public B putAll(Map<String, Object> values) {
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        object.put(entry.getKey(), entry.getValue());
      }
      return clazz.cast(this);
    }

  }

}

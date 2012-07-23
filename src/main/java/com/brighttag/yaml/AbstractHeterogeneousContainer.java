package com.brighttag.yaml;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Base class for heterogeneous containers.
 *
 * @param <T> the type of the key
 * @author codyaray
 * @since 7/19/12
 */
public abstract class AbstractHeterogeneousContainer<T> implements HeterogeneousContainer<T> {

  @Override
  public Object get(T key) throws HeterogeneousException {
    Object o = opt(key);
    if (o == null) {
      throw exception(key, "not found");
    }
    return o;
  }

  /*
   * String Type
   */

  @Override
  public String getString(T key) throws HeterogeneousException {
    return get(key).toString();
  }

  @Override
  public @Nullable String optString(T key, @Nullable String defaultValue) {
    try {
      return getString(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public @Nullable String optString(T key) {
    // Note: this differs from Jettison's JSON containers that default to an empty string
    return optString(key, null);
  }

  /*
   * Boolean Type
   */

  @Override
  public Boolean getBoolean(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o.equals(Boolean.FALSE) ||
        (o instanceof String &&
        ((String) o).equalsIgnoreCase("false"))) {
      return false;
    } else if (o.equals(Boolean.TRUE) ||
        (o instanceof String &&
        ((String) o).equalsIgnoreCase("true"))) {
      return true;
    }
    throw exception(key, "is not a Boolean.");
  }

  @Override
  public @Nullable Boolean optBoolean(T key, @Nullable Boolean defaultValue) {
    try {
      return getBoolean(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public Boolean optBoolean(T key) {
    return optBoolean(key, false);
  }

  /*
   * Integer Type
   */

  @Override
  public Integer getInt(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o instanceof Number) {
      return ((Number) o).intValue();
    } else if (o instanceof String) {
      try {
        return Integer.valueOf((String) o);
      } catch (NumberFormatException e) {
        throw nanException(key);
      }
    }
    throw nanException(key);
  }

  @Override
  public @Nullable Integer optInt(T key, @Nullable Integer defaultValue) {
    try {
      return getInt(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public Integer optInt(T key) {
    return optInt(key, 0);
  }

  /*
   * Double Type
   */

  @Override
  public Double getDouble(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o instanceof Number) {
      return ((Number) o).doubleValue();
    } else if (o instanceof String) {
      try {
        return Double.valueOf((String) o);
      } catch (NumberFormatException e) {
        throw nanException(key);
      }
    }
    throw nanException(key);
  }

  @Override
  public @Nullable Double optDouble(T key, @Nullable Double defaultValue) {
    try {
      return getDouble(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public Double optDouble(T key) {
    return optDouble(key, Double.NaN);
  }

  /*
   * Long Type
   */

  @Override
  public Long getLong(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o instanceof Number) {
      return ((Number) o).longValue();
    } else if (o instanceof String) {
      try {
        return Long.valueOf((String) o);
      } catch (NumberFormatException e) {
        throw nanException(key);
      }
    }
    throw nanException(key);
  }

  @Override
  public @Nullable Long optLong(T key, @Nullable Long defaultValue) {
    try {
      return getLong(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public Long optLong(T key) {
    return optLong(key, 0L);
  }

  /*
   * HeterogeneousArray Type
   */

  @Override
  public HeterogeneousArray getArray(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o instanceof HeterogeneousArray) {
      return (HeterogeneousArray) o;
    } else if (o instanceof List) {
      /*
       * This cast is always safe; we first check instanceof List.
       * We only put List<Object> into the HeterogenousList.
       */
      @SuppressWarnings("unchecked")
      List<Object> objects = (List<Object>) o;
      return new SimpleHeterogeneousArray(objects);
    }
    throw exception(key, "is not a HeterogeneousArray");
  }

  @Override
  public @Nullable HeterogeneousArray optArray(T key, @Nullable HeterogeneousArray defaultValue) {
    try {
      return getArray(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public @Nullable HeterogeneousArray optArray(T key) {
    return optArray(key, null);
  }

  /*
   * HeterogeneousMap Type
   */

  @Override
  public HeterogeneousMap getMap(T key) throws HeterogeneousException {
    Object o = get(key);
    if (o instanceof HeterogeneousMap) {
      return (HeterogeneousMap) o;
    } else if (o instanceof Map) {
      /*
       * This cast is always safe; we first check instanceof Map.
       * We only put Map<String, Object> into the HeterogenousMap.
       */
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) o;
      return new SimpleHeterogeneousMap(map);
    }
    throw exception(key, "is not a HeterogeneousMap");
  }

  @Override
  public @Nullable HeterogeneousMap optMap(T key, @Nullable HeterogeneousMap defaultValue) {
    try {
      return getMap(key);
    } catch (HeterogeneousException e) {
      return defaultValue;
    }
  }

  @Override
  public @Nullable HeterogeneousMap optMap(T key) {
    return optMap(key, null);
  }

  private HeterogeneousException nanException(T key) {
    return exception(key, "is not a number.");
  }

  protected HeterogeneousException exception(T key, String message) {
    return new HeterogeneousException(String.format("%s[%s] %s", getClass().getSimpleName(), key, message));
  }

}

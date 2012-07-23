package com.brighttag.yaml;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.yaml.snakeyaml.Yaml;

/**
 * An ordered sequence of values whose external form is a string
 * conforming to the Yaml specification.
 *
 * @author codyaray
 * @since 7/13/12
 */
public class YamlArray extends AbstractHeterogeneousArray {

  public YamlArray() {
    this(ImmutableList.of());
  }

  @SuppressWarnings("unchecked")
  public YamlArray(InputStream inputStream) {
    this((List<Object>) new Yaml().load(inputStream));
  }

  YamlArray(List<Object> array) {
    super(array);
  }

  private YamlArray(Builder builder) {
    super(builder);
  }

  /*
   * Override methods to narrow the exception and return types for Yaml.
   * If Java supported multiple inheritance, could push up to a superclass.
   */

  @Override
  public Object get(Integer index) throws YamlException {
    try {
      return super.get(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public String getString(Integer index) throws YamlException {
    try {
      return super.getString(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public Boolean getBoolean(Integer index) throws YamlException {
    try {
      return super.getBoolean(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public Integer getInt(Integer index) throws YamlException {
    try {
      return super.getInt(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public Double getDouble(Integer index) throws YamlException {
    try {
      return super.getDouble(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public Long getLong(Integer index) throws YamlException {
    try {
      return super.getLong(index);
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public YamlArray getArray(Integer index) throws YamlException {
    try {
      return new YamlArray(super.getArray(index).asList());
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public @Nullable YamlArray optArray(Integer index, @Nullable HeterogeneousArray defaultValue) {
    HeterogeneousArray array = super.optArray(index, defaultValue);
    return (array == null) ? null : new YamlArray(array.asList());
  }

  @Override
  public @Nullable YamlArray optArray(Integer index) {
    HeterogeneousArray array = super.optArray(index);
    return (array == null) ? null : new YamlArray(array.asList());
  }

  @Override
  public YamlObject getMap(Integer index) throws YamlException {
    try {
      return new YamlObject(super.getMap(index).asMap());
    } catch (HeterogeneousException e) {
      throw new YamlException(e.getMessage());
    }
  }

  @Override
  public @Nullable YamlObject optMap(Integer index, @Nullable HeterogeneousMap defaultValue) {
    HeterogeneousMap object = super.optMap(index, defaultValue);
    return (object == null) ? null : new YamlObject(object.asMap());
  }

  @Override
  public @Nullable YamlObject optMap(Integer index) {
    HeterogeneousMap object = super.optMap(index);
    return (object == null) ? null : new YamlObject(object.asMap());
  }

  /**
   * @author codyaray
   * @since 7/22/12
   */
  public static class Builder extends AbstractHeterogeneousArray.Builder<Builder> {

    public Builder() {
      super(Builder.class);
    }

    public YamlArray build() {
      return new YamlArray(this);
    }

  }

}

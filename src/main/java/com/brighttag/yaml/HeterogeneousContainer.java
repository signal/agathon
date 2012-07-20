package com.brighttag.yaml;

import javax.annotation.Nullable;

/**
 * A container of values with different types.
 *
 * @param <T> the type of the key
 * @author codyaray
 * @since 7/19/12
 */
public interface HeterogeneousContainer<T> {

  /**
   * Get an optional value associated with a key.
   *
   * @param key   A key string.
   * @return      An object which is the value, or {@code null} if there is no value.
   */
  @Nullable Object opt(T key);

  /**
   * Get the value object associated with a key.
   *
   * @param key   A key string.
   * @return      The object associated with the key.
   * @throws      HeterogeneousException if the key is not found.
   */
  Object get(T key) throws HeterogeneousException;

  /**
   * Get the string associated with a key.
   *
   * @param key   A key string.
   * @return      A string which is the value.
   * @throws      HeterogeneousException if the key is not found.
   */
  String getString(T key) throws HeterogeneousException;

  /**
   * Get an optional string associated with a key.
   * It returns the defaultValue if there is no such key.
   *
   * @param key            A key string.
   * @param defaultValue   The default.
   * @return               A string which is the value.
   */
  @Nullable String optString(T key, @Nullable String defaultValue);

  /**
   * Get an optional string associated with a key.
   * It returns {@code null} if there is no such key. If the value is not
   * a string and is not null, then it is converted to a string.
   *
   * @param key   A key string.
   * @return      A string which is the value.
   */
  @Nullable String optString(T key);

  /**
   * Get the boolean value associated with a key.
   *
   * @param key   A key string.
   * @return      The truth.
   * @throws      HeterogeneousException if the value is not a Boolean or the String "true" or "false".
   */
  Boolean getBoolean(T key) throws HeterogeneousException;

  /**
   * Get an optional boolean associated with a key.
   * It returns the defaultValue if there is no such key, or if it is not
   * a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param key              A key string.
   * @param defaultValue     The default.
   * @return                 The truth.
   */
  @Nullable Boolean optBoolean(T key, @Nullable Boolean defaultValue);

  /**
   * Get an optional boolean associated with a key.
   * It returns false if there is no such key, or if the value is not
   * Boolean.TRUE or the String "true".
   *
   * @param key   A key string.
   * @return      The truth.
   */
  Boolean optBoolean(T key);

  /**
   * Get the int value associated with a key. If the number value is too
   * large for an int, it will be clipped.
   *
   * @param key   A key string.
   * @return      The integer value.
   * @throws      HeterogeneousException if the key is not found or if the value
   *              cannot be converted to an integer.
   */
  Integer getInt(T key) throws HeterogeneousException;

  /**
   * Get an optional int value associated with a key,
   * or the default if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key              A key string.
   * @param defaultValue     The default.
   * @return                 An object which is the value.
   */
  @Nullable Integer optInt(T key, @Nullable Integer defaultValue);

  /**
   * Get an optional int value associated with a key,
   * or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A key string.
   * @return      An object which is the value.
   */
  Integer optInt(T key);

  /**
   * Get the double value associated with a key.
   * @param key   A key string.
   * @return      The numeric value.
   * @throws      HeterogeneousException if the key is not found or
   *  if the value is not a Number object and cannot be converted to a number.
   */
  Double getDouble(T key) throws HeterogeneousException;

  /**
   * Get an optional double associated with a key, or the
   * defaultValue if there is no such key or if its value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A key string.
   * @param defaultValue     The default.
   * @return      An object which is the value.
   */
  @Nullable Double optDouble(T key, @Nullable Double defaultValue);

  /**
   * Get an optional double associated with a key,
   * or NaN if there is no such key or if its value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A string which is the key.
   * @return      An object which is the value.
   */
  Double optDouble(T key);

  /**
   * Get the long value associated with a key. If the number value is too
   * long for a long, it will be clipped.
   *
   * @param key   A key string.
   * @return      The long value.
   * @throws      HeterogeneousException if the key is not found or if the value
   *              cannot be converted to a long.
   */
  Long getLong(T key) throws HeterogeneousException;

  /**
   * Get an optional long value associated with a key,
   * or the default if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A key string.
   * @param defaultValue     The default.
   * @return      An object which is the value.
   */
  @Nullable Long optLong(T key, @Nullable Long defaultValue);

  /**
   * Get an optional long value associated with a key,
   * or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A key string.
   * @return      An object which is the value.
   */
  Long optLong(T key);

  /**
   * Get the YamlArray value associated with a key.
   *
   * @param key   A key string.
   * @return      A YamlArray which is the value.
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlArray.
   */
  HeterogeneousArray getArray(T key) throws HeterogeneousException;

  /**
   * Get an optional YamlArray associated with a key,
   * or the default if there is no such key or if the value is not a YamlArray.
   *
   * @param key   A key string.
   * @return      A YamlArray which is the value.
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlArray.
   */
  @Nullable HeterogeneousArray optArray(T key, @Nullable HeterogeneousArray defaultValue);

  /**
   * Get an optional YamlArray associated with a key.
   * It returns null if there is no such key, or if its value is not a
   * YamlArray.
   *
   * @param key   A key string.
   * @return      A YamlArray which is the value.
   */
  @Nullable HeterogeneousArray optArray(T key);

  /**
   * Get the YamlObject value associated with a key.
   *
   * @param key   A key string.
   * @return      A YamlObject which is the value.
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlObject.
   */
  HeterogeneousMap getMap(T key) throws HeterogeneousException;

  /**
   *
   * @param key   A key string.
   * @param defaultValue
   * @return      A YamlObject which is the value.
   */
  @Nullable HeterogeneousMap optMap(T key, @Nullable HeterogeneousMap defaultValue);

  /**
   * Get an optional YamlObject associated with a key.
   * It returns null if there is no such key, or if its value is not a
   * YamlObject.
   *
   * @param key   A key string.
   * @return      A YamlObject which is the value.
   */
  @Nullable HeterogeneousMap optMap(T key);

}

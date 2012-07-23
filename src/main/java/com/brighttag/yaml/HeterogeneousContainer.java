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
   * Get the optional value associated with the key.
   *
   * @param key   the key
   * @return      An object which is the value as a value, or {@code null} if there is no
   */
  @Nullable Object opt(T key);

  /**
   * Get the required value associated with the key.
   *
   * @param key   the key
   * @return      The object associated with the key.
   * @throws      HeterogeneousException if the key is not found.
   */
  Object get(T key) throws HeterogeneousException;

  /**
   * Get the string associated with the key.
   *
   * @param key   the key
   * @return      the value as a string
   * @throws      HeterogeneousException if the key is not found.
   */
  String getString(T key) throws HeterogeneousException;

  /**
   * Get an optional string associated with the key.
   * It returns the default value if there is no such key.
   *
   * @param key            the key
   * @param defaultValue   the default value
   * @return               the value as a string
   */
  @Nullable String optString(T key, @Nullable String defaultValue);

  /**
   * Get an optional string associated with the key
   * It returns {@code null} if there is no such key. If the value is not
   * a string and is not null, then it is converted to a string.
   *
   * @param key   the key
   * @return      the value as a string
   */
  @Nullable String optString(T key);

  /**
   * Get the boolean value associated with the key.
   *
   * @param key   the key
   * @return      The truth.
   * @throws      HeterogeneousException if the value is not a Boolean or the String "true" or "false".
   */
  Boolean getBoolean(T key) throws HeterogeneousException;

  /**
   * Get an optional boolean associated with the key
   * It returns the default valuealue if there is no such key, or if it is not
   * a Boolean or the String "true" or "false" (case insensitive).
   *
   * @param key              the key
   * @param defaultValue     the default value
   * @return                 The truth.
   */
  @Nullable Boolean optBoolean(T key, @Nullable Boolean defaultValue);

  /**
   * Get an optional boolean associated with the key
   * It returns false if there is no such key, or if the value is not
   * Boolean.TRUE or the String "true".
   *
   * @param key   the key
   * @return      The truth.
   */
  Boolean optBoolean(T key);

  /**
   * Get the int value associated with the key If the number value is too
   * large for an int, it will be clipped.
   *
   * @param key   the key
   * @return      the value as a integer
   * @throws      HeterogeneousException if the key is not found or if the value
   *              cannot be converted to an integer.
   */
  Integer getInt(T key) throws HeterogeneousException;

  /**
   * Get an optional int value associated with the key
   * or the default valueif there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key              the key
   * @param defaultValue     the default value
   * @return                 the value as an integer
   */
  @Nullable Integer optInt(T key, @Nullable Integer defaultValue);

  /**
   * Get an optional int value associated with the key
   * or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   the key
   * @return      the value as an integer
   */
  Integer optInt(T key);

  /**
   * Get the double value associated with the key.
   *
   * @param key   the key
   * @return      the value as a double
   * @throws      HeterogeneousException if the key is not found or
   *  if the value is not a Number object and cannot be converted to a number.
   */
  Double getDouble(T key) throws HeterogeneousException;

  /**
   * Get an optional double associated with the key or the
   * defaultValue if there is no such key or if its value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   the key
   * @param defaultValue     the default value
   * @return      the value as a double
   */
  @Nullable Double optDouble(T key, @Nullable Double defaultValue);

  /**
   * Get an optional double associated with the key
   * or NaN if there is no such key or if its value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   A string which is the key.
   * @return      the value as a double
   */
  Double optDouble(T key);

  /**
   * Get the long value associated with the key If the number value is too
   * long for a long, it will be clipped.
   *
   * @param key   the key
   * @return      the value as a long
   * @throws      HeterogeneousException if the key is not found or if the value
   *              cannot be converted to a long.
   */
  Long getLong(T key) throws HeterogeneousException;

  /**
   * Get an optional long value associated with the key
   * or the default valueif there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   the key
   * @param defaultValue     the default value
   * @return      the value as a long
   */
  @Nullable Long optLong(T key, @Nullable Long defaultValue);

  /**
   * Get an optional long value associated with the key
   * or zero if there is no such key or if the value is not a number.
   * If the value is a string, an attempt will be made to evaluate it as
   * a number.
   *
   * @param key   the key
   * @return      the value as a long
   */
  Long optLong(T key);

  /**
   * Get the YamlArray value associated with the key.
   *
   * @param key   the key
   * @return      the value as a YamlArray
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlArray.
   */
  HeterogeneousArray getArray(T key) throws HeterogeneousException;

  /**
   * Get an optional YamlArray associated with the key
   * or the default valueif there is no such key or if the value is not a YamlArray.
   *
   * @param key   the key
   * @return      the value as a YamlArray
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlArray.
   */
  @Nullable HeterogeneousArray optArray(T key, @Nullable HeterogeneousArray defaultValue);

  /**
   * Get an optional YamlArray associated with the key
   * It returns null if there is no such key, or if its value is not a
   * YamlArray.
   *
   * @param key   the key
   * @return      the value as a YamlArray
   */
  @Nullable HeterogeneousArray optArray(T key);

  /**
   * Get the YamlObject value associated with the key.
   *
   * @param key   the key
   * @return      the value as a YamlObject
   * @throws      HeterogeneousException if the key is not found or if the value is not a YamlObject.
   */
  HeterogeneousMap getMap(T key) throws HeterogeneousException;

  /**
   *
   * @param key           the key
   * @param defaultValue  the default value
   * @return              the value as a YamlObject
   */
  @Nullable HeterogeneousMap optMap(T key, @Nullable HeterogeneousMap defaultValue);

  /**
   * Get an optional YamlObject associated with the key
   * It returns null if there is no such key, or if its value is not a
   * YamlObject.
   *
   * @param key   the key
   * @return      the value as a YamlObject
   */
  @Nullable HeterogeneousMap optMap(T key);

}

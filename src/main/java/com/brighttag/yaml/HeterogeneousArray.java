package com.brighttag.yaml;

import java.util.List;

/**
 * An ordered sequence of values whose type may vary. The values can be any of
 * these types: {@link Boolean}, {@link Number}, {@link String}, {@link HeterogeneousArray},
 * or {@link HeterogeneousMap}. The internal form is an object having {@code get} and
 * {@code opt} methods for accessing the values by index.
 *
 * Different implementations convert an external form text into an internal form
 * whose values can be retrieved with the {@code get} and {@code opt} methods.
 * A {@code get} method returns a value if one can be found, and throws an
 * exception if one cannot be found. An {@code opt} method returns a default value
 * instead of throwing an exception, and so is useful for obtaining optional values.
 *
 * The generic {@code get()} and {@code opt()} methods return an object, which you can
 * cast or query for type. There are also typed {@code get} and {@code opt} methods that
 * do type checking and type coercion for you.
 *
 * TODO: Add a builder with {@code put} methods for adding or replacing values by index.
 *
 * @author codyaray
 * @since 7/19/12
 */
public interface HeterogeneousArray extends HeterogeneousContainer<Integer> {

  /**
   * Get the number of elements in the HeterogeneousArray, including {@code nulls}.
   *
   * @return The length (or size).
   */
  int size();

  List<Object> asList();
}

package com.brighttag.agathon.resources.yaml;

import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Factory pattern for converting POJOs into YAML.
 *
 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/21/12
 */
public interface YamlWriter<T> {

  /**
   * Returns a YAML representation for an object.
   * @param obj the object
   * @return the YamlObject
   * @throws YamlException if the object cannot be rendered as YAML.
   */
  YamlObject toYaml(T obj) throws YamlException;

}

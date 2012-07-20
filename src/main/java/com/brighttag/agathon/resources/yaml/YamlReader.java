package com.brighttag.agathon.resources.yaml;

import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Factory pattern for converting Yaml into POJOs.
 *
 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/8/12
 */
public interface YamlReader<T> {

  /**
   * Returns an object from a YAML representation.
   * @param obj the YAML object
   * @return the new Object
   * @throws YamlException if the state of the YAML is not correct enough to render the object.
   */
  T fromYaml(YamlObject obj) throws YamlException;

}

package com.brighttag.agathon.resources.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.google.common.base.Throwables;

import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Base class for Jersey YAML readers.

 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/15/12
 */
public abstract class AbstractYamlReader<T> implements YamlReader<T>, MessageBodyReader<T> {

  private final Type t;

  public AbstractYamlReader() {
    /*
     * Use reflection to get the "Super Type Token"
     * See http://gafter.blogspot.com/2006/12/super-type-tokens.html
     */

    // Because this class is abstract, getClass() must return a sub-class of it.
    // Traverse the hierarchy from the instantiated subclass to a direct subclass of this one
    Class<?> klass = getClass();
    while (!klass.getSuperclass().equals(AbstractYamlReader.class)) {
      klass = klass.getSuperclass();
    }

     // This operation is safe. Because klass is a direct sub-class, getGenericSuperclass() will
     // always return the Type of this class. Because this class is parameterized, the cast is safe
    ParameterizedType superclass = (ParameterizedType) klass.getGenericSuperclass();
    t = superclass.getActualTypeArguments()[0];
  }

  @Override
  public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream) throws IOException {
    try {
      return fromYaml(new YamlObject(entityStream));
    } catch (YamlException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return "application/yaml".equals(mediaType.toString()) && genericType.equals(t);
  }

}

package com.brighttag.agathon.resources.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.google.common.base.Throwables;
import com.google.common.reflect.TypeToken;

import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Base class for Jersey YAML readers.

 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/15/12
 */
public abstract class AbstractYamlReader<T> implements YamlReader<T>, MessageBodyReader<T> {

  @SuppressWarnings("serial")
  private final TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
  private final Type type = typeToken.getType();

  @Override
  public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream) throws IOException {
    return readFrom(entityStream);
  }

  public T readFrom(InputStream entityStream) throws IOException {
    try {
      return fromYaml(new YamlObject(entityStream));
    } catch (YamlException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return MediaTypes.APPLICATION_YAML_TYPE.equals(mediaType) && this.type.equals(genericType);
  }

}

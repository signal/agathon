package com.brighttag.agathon.resources.yaml;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.reflect.TypeToken;

import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;

/**
 * Base class for Jersey YAML writers.
 *
 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/22/12
 */
public abstract class AbstractYamlWriter<T> implements YamlWriter<T>, MessageBodyWriter<T> {

  @SuppressWarnings("serial")
  private final TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
  private final Type type = typeToken.getType();

  @Override
  public void writeTo(T obj, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream) throws IOException {
    try {
      entityStream.write(toYaml(obj).toString().getBytes(Charsets.UTF_8));
    } catch (YamlException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return MediaTypes.APPLICATION_YAML_TYPE.equals(mediaType) && this.type.equals(genericType);
  }

  @Override
  public long getSize(T obj, Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

}

/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * A Jersey provider which adds validation to the basic Jackson Json provider. Any request
 * entity method parameters annotated with {@code @Valid} are validated, and an informative
 * {@code 422 Unprocessable Entity} response is returned should the entity be invalid.
 * <br/>
 * Thanks to Yammer's Dropwizard for the original idea.
 *
 * @author codyaray
 * @since 5/23/12
 */
@Provider
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
public class ValidatingJacksonJsonProvider
    implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

  // Unfortunate that this isn't defined in Response.Status
  @VisibleForTesting static final StatusType UNPROCESSABLE_ENTITY = new StatusType() {
    @Override
    public int getStatusCode() {
      return 422;
    }

    @Override
    public Response.Status.Family getFamily() {
      return Response.Status.Family.CLIENT_ERROR;
    }

    @Override
    public String getReasonPhrase() {
      return "Unprocessable Entity";
    }
  };

  private final JacksonJsonProvider delegate;
  private final Validator validator;

  @Inject
  public ValidatingJacksonJsonProvider(JacksonJsonProvider delegate, Validator validator) {
    this.delegate = delegate;
    this.validator = validator;
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
    throws IOException {

    Object value = parseEntity(type, genericType, annotations, mediaType, httpHeaders, entityStream);

    if (hasValidAnnotation(annotations)) {
      List<String> errors = validate(value);
      if (!errors.isEmpty()) {
        StringBuilder msg = new StringBuilder("The request entity had the following errors:\n");
        for (String error : errors) {
          msg.append("  * ").append(error).append('\n');
        }
        throw new WebApplicationException(unprocessableEntity(msg.toString()));
      }
    }

    return value;
  }

  private Object parseEntity(Class<Object> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
    throws IOException {
    return delegate.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

  @Override
  public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
    throws IOException {
    delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return delegate.isWriteable(type, genericType, annotations, mediaType);
  }

  @Override
  public long getSize(Object t, Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return delegate.getSize(t, type, genericType, annotations, mediaType);
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType) {
    return delegate.isReadable(type, genericType, annotations, mediaType);
  }

  private List<String> validate(Object o) {
    Set<String> errors = Sets.newHashSet();
    Set<ConstraintViolation<Object>> violations = validator.validate(o);
    for (ConstraintViolation<Object> v : violations) {
      errors.add(String.format("%s %s (was %s)",
          v.getPropertyPath(), v.getMessage(), v.getInvalidValue()));
    }
    return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
  }

  @VisibleForTesting static boolean hasValidAnnotation(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (Valid.class.equals(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }

  private static Response unprocessableEntity(String msg) {
    return Response
        .status(UNPROCESSABLE_ENTITY)
        .entity(msg)
        .type(MediaType.TEXT_PLAIN_TYPE)
        .build();
  }

}

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

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 5/23/12
 */
public class ValidatingJacksonJsonProviderTest extends EasyMockSupport {

  private ValidatingJacksonJsonProvider provider;
  private JacksonJsonProvider delegate;
  private Validator validator;

  private Object value;
  private Class<Object> type;
  private Type genericType;
  private Annotation[] annotations;
  private MediaType mediaType;

  @Before
  public void setUp() {
    delegate = createMock(JacksonJsonProvider.class);
    validator = createMock(Validator.class);
    provider = new ValidatingJacksonJsonProvider(delegate, validator);

    value = createMock(Object.class);
    type = Object.class;
    genericType = createMock(Type.class);
    annotations = new Annotation[] { createMock(Annotation.class) };
    mediaType = createMock(MediaType.class);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void readFrom_noValidation_success() throws Exception {
    annotations = new Annotation[] { createMock(Annotation.class) };
    @SuppressWarnings("unchecked")
    MultivaluedMap<String, String> httpHeaders = createMock(MultivaluedMap.class);
    InputStream entityStream = createMock(InputStream.class);

    expect(annotations[0].annotationType()).andStubReturn(null);
    expect(delegate.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream))
        .andReturn(value);
    replayAll();

    assertEquals(value,
        provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream));
  }

  @Test(expected = JsonParseException.class)
  public void readFrom_noValidation_jsonParseError() throws Exception {
    annotations = new Annotation[] { createMock(Annotation.class) };
    @SuppressWarnings("unchecked")
    MultivaluedMap<String, String> httpHeaders = createMock(MultivaluedMap.class);
    InputStream entityStream = createMock(InputStream.class);

    expect(annotations[0].annotationType()).andStubReturn(null);
    expect(delegate.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream))
        .andThrow(createMock(JsonParseException.class));
    replayAll();

    provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

  @Test
  public void readFrom_withValidation_success() throws Exception {
    annotations = new Annotation[] { createMockAnnotation(Valid.class) };
    @SuppressWarnings("unchecked")
    MultivaluedMap<String, String> httpHeaders = createMock(MultivaluedMap.class);
    InputStream entityStream = createMock(InputStream.class);

    expect(delegate.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream))
        .andReturn(value);
    expect(validator.validate(value)).andReturn(ImmutableSet.<ConstraintViolation<Object>>of());
    replayAll();

    assertEquals(value,
        provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void readFrom_withValidation_failure() throws Exception {
    annotations = new Annotation[] { createMockAnnotation(Valid.class) };
    MultivaluedMap<String, String> httpHeaders = createMock(MultivaluedMap.class);
    InputStream entityStream = createMock(InputStream.class);

    ConstraintViolation<Object> violation1 = createNiceMock(ConstraintViolation.class);
    ConstraintViolation<Object> violation2 = createNiceMock(ConstraintViolation.class);
    Set<ConstraintViolation<Object>> violations = ImmutableSet.of(violation1, violation2);

    expect(delegate.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream))
        .andReturn(value);
    expect(validator.validate(value)).andReturn(violations);
    replayAll();

    try {
      provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
      fail("Expected WebApplicationException");
    } catch (WebApplicationException e) {
      assertEquals(ValidatingJacksonJsonProvider.UNPROCESSABLE_ENTITY.getStatusCode(),
          e.getResponse().getStatus());
      assertEquals(MediaType.TEXT_PLAIN_TYPE,
          e.getResponse().getMetadata().getFirst(HttpHeaders.CONTENT_TYPE));
      assertNotNull(e.getResponse().getEntity());
    }
  }

  @Test
  public void hasValidAnnotation_false() {
    Annotation annotation = createMockAnnotation(Annotation.class);
    Annotation[] annotations = new Annotation[] { annotation };
    replayAll();

    assertFalse(ValidatingJacksonJsonProvider.hasValidAnnotation(annotations));
  }

  @Test
  public void hasValidAnnotation_true() {
    Annotation annotation = createMockAnnotation(Valid.class);
    Annotation[] annotations = new Annotation[] { annotation };
    replayAll();

    assertTrue(ValidatingJacksonJsonProvider.hasValidAnnotation(annotations));
  }

  @Test
  public void writeTo() throws Exception {
    Object value = createMock(Object.class);
    @SuppressWarnings("unchecked")
    MultivaluedMap<String, Object> httpHeaders = createMock(MultivaluedMap.class);
    OutputStream entityStream = createMock(OutputStream.class);
    delegate.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    replayAll();

    provider.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

  @Test
  public void getSize() {
    Object value = createMock(Object.class);
    expect(delegate.getSize(value, type, genericType, annotations, mediaType)).andReturn(-1L);
    replayAll();

    assertEquals(-1L, provider.getSize(value, type, genericType, annotations, mediaType));
  }

  @Test
  public void isReadable() {
    expect(delegate.isReadable(type, genericType, annotations, mediaType)).andReturn(true);
    replayAll();

    assertTrue(provider.isReadable(type, genericType, annotations, mediaType));
  }

  @Test
  public void isWriteable() {
    expect(delegate.isWriteable(type, genericType, annotations, mediaType)).andReturn(true);
    replayAll();

    assertTrue(provider.isWriteable(type, genericType, annotations, mediaType));
  }

  /**
   * Necessary to "instantiate" an annotation, since mocking #annotationType() fails.
   * @see http://stackoverflow.com/questions/2786292
   */
  private <T extends Annotation> Annotation createMockAnnotation(final Class<T> klass) {
    return (Annotation) Proxy.newProxyInstance(
        klass.getClassLoader(),
        new Class[] { Annotation.class },
        new InvocationHandler() {
          @Override public Object invoke(Object proxy, Method method, Object[] args) {
            return klass; // only getClass() or annotationType() should be called.
          }
        });
  }

}

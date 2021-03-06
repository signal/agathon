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

package com.brighttag.agathon.dao.sdb;

import java.util.List;
import java.util.Set;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 5/15/12
 */
public class SdbCassandraInstanceDaoTest extends EasyMockSupport {

  private static final int ID = 1;
  private static final String DATACENTER = "dc";
  private static final String RACK = "rack";
  private static final String HOSTNAME = "host";
  private static final String PUBLIC_IP_ADDRESS = "publicIpAddress";
  private static final String FULLY_QUALIFIED_DOMAIN_NAME = "fullyQualifiedDomainName";
  private static final String NEXT_TOKEN = "nextToken";
  private static final String DOMAIN_NAMESPACE = "Production";
  private static final String RING_NAME = "ProfileStore";
  private static final String DOMAIN = "CassandraInstances." + DOMAIN_NAMESPACE + "." + RING_NAME;
  private static final String ALL_QUERY = String.format(SdbCassandraInstanceDao.ALL_QUERY, DOMAIN);

  private AmazonSimpleDBClient simpleDbClient;
  private SdbCassandraInstanceDao dao;
  private CassandraDomainFactory domainFactory;

  @Before
  public void setUp() {
    simpleDbClient = createMock(AmazonSimpleDBClient.class);
    domainFactory = createMock(CassandraDomainFactory.class);
    dao = new SdbCassandraInstanceDao(simpleDbClient, domainFactory);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void findAll() {
    List<Item> items = createItems();
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>();

    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME));
    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items);
    expect(result.getNextToken()).andReturn(null);
    replayAll();

    Set<CassandraInstance> expected = transform(items);
    assertEquals(expected, dao.findAll(RING_NAME));

    assertEquals(ALL_QUERY, requestCapture.getValue().getSelectExpression());
    assertNull(requestCapture.getValue().getNextToken());
  }

  @Test
  public void findAll_paginated() {
    List<Item> items1 = createItems(0);
    List<Item> items2 = createItems(10);
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>(CaptureType.ALL);

    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME)).times(2);
    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items1);
    expect(result.getNextToken()).andReturn(NEXT_TOKEN);

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items2);
    expect(result.getNextToken()).andReturn(null);
    replayAll();

    Set<CassandraInstance> expected = Sets.newHashSet(
        Iterables.concat(transform(items1), transform(items2)));
    assertEquals(expected, dao.findAll(RING_NAME));

    List<SelectRequest> requests = requestCapture.getValues();
    assertEquals(2, requests.size());
    assertEquals(ALL_QUERY, requests.get(0).getSelectExpression());
    assertNull(requests.get(0).getNextToken());
    assertEquals(ALL_QUERY, requests.get(1).getSelectExpression());
    assertEquals(NEXT_TOKEN, requests.get(1).getNextToken());
  }

  @Test
  public void findById() {
    List<Item> items = ImmutableList.of(createItem(1));
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>();

    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME));
    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andStubReturn(items);
    replayAll();

    Set<CassandraInstance> expected = transform(items);
    assertEquals(expected.iterator().next(), dao.findById(RING_NAME, ID));

    assertEquals(String.format(SdbCassandraInstanceDao.INSTANCE_QUERY, DOMAIN, ID),
        requestCapture.getValue().getSelectExpression());
    assertNull(requestCapture.getValue().getNextToken());
  }

  @Test
  public void findById_notFound() {
    List<Item> items = ImmutableList.of();
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>();

    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME));
    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andStubReturn(items);
    replayAll();

    assertNull(dao.findById(RING_NAME, ID));
  }

  @Test
  public void save() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    Capture<PutAttributesRequest> requestCapture = new Capture<PutAttributesRequest>();
    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME));
    expect(instance.getId()).andReturn(ID).times(2);
    expect(instance.getDataCenter()).andReturn(DATACENTER);
    expect(instance.getRack()).andReturn(RACK);
    expect(instance.getHostName()).andReturn(HOSTNAME);
    expect(instance.getPublicIpAddress()).andReturn(PUBLIC_IP_ADDRESS);
    expect(instance.getFullyQualifiedDomainName()).andReturn(FULLY_QUALIFIED_DOMAIN_NAME);
    simpleDbClient.putAttributes(capture(requestCapture));
    replayAll();

    dao.save(RING_NAME, instance);

    PutAttributesRequest request = requestCapture.getValue();
    assertEquals(DOMAIN, request.getDomainName());
    assertEquals(String.valueOf(ID), request.getItemName());
    assertReplaceableAttributes(request);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    Capture<DeleteAttributesRequest> requestCapture = new Capture<DeleteAttributesRequest>();
    expect(domainFactory.createFromRing(RING_NAME)).andReturn(domain(RING_NAME));
    expect(instance.getId()).andReturn(ID).times(2);
    expect(instance.getDataCenter()).andReturn(DATACENTER);
    expect(instance.getRack()).andReturn(RACK);
    expect(instance.getHostName()).andReturn(HOSTNAME);
    expect(instance.getPublicIpAddress()).andReturn(PUBLIC_IP_ADDRESS);
    expect(instance.getFullyQualifiedDomainName()).andReturn(FULLY_QUALIFIED_DOMAIN_NAME);
    simpleDbClient.deleteAttributes(capture(requestCapture));
    replayAll();

    dao.delete(RING_NAME, instance);

    DeleteAttributesRequest request = requestCapture.getValue();
    assertEquals(DOMAIN, request.getDomainName());
    assertEquals(String.valueOf(ID), request.getItemName());
    assertAttributes(request);
  }

  @Test
  public void transform() {
    Item item = createItem(1);
    replayAll();
    CassandraInstance instance = SdbCassandraInstanceDao.transform(item);
    assertEquals(1, instance.getId());
    assertEquals("dc1", instance.getDataCenter());
    assertEquals("rack1", instance.getRack());
    assertEquals("host1", instance.getHostName());
    assertEquals("publicIpAddress1", instance.getPublicIpAddress());
  }

  private CassandraDomain domain(String ring) {
    return new CassandraDomain(DOMAIN_NAMESPACE, ring);
  }

  private List<Item> createItems() {
    return createItems(0);
  }

  private List<Item> createItems(int offset) {
    return ImmutableList.of(createItem(offset + 1), createItem(offset + 2), createItem(offset + 3));
  }

  private Item createItem(int ordinal) {
    Item item = createMock(Item.class);
    expect(item.getAttributes()).andStubReturn(createAttributes(ordinal));
    return item;
  }

  private List<Attribute> createAttributes(int ordinal) {
    return ImmutableList.of(
        createAttribute(SdbCassandraInstanceDao.ID_KEY, String.valueOf(ordinal)),
        createAttribute(SdbCassandraInstanceDao.DATACENTER_KEY, DATACENTER + ordinal),
        createAttribute(SdbCassandraInstanceDao.RACK_KEY, RACK + ordinal),
        createAttribute(SdbCassandraInstanceDao.HOSTNAME_KEY, HOSTNAME + ordinal),
        createAttribute(SdbCassandraInstanceDao.PUBLIC_IP_ADDRESS_KEY, PUBLIC_IP_ADDRESS + ordinal));
  }

  private Attribute createAttribute(String key, String value) {
    Attribute attribute = createMock(Attribute.class);
    expect(attribute.getName()).andStubReturn(key);
    expect(attribute.getValue()).andStubReturn(value);
    return attribute;
  }

  private static Set<CassandraInstance> transform(List<Item> items) {
    Set<CassandraInstance> instances = Sets.newHashSet();
    for (Item item : items) {
      instances.add(SdbCassandraInstanceDao.transform(item));
    }
    return instances;
  }

  // Checkstyle ignore: CyclomaticComplexity
  private static void assertReplaceableAttributes(PutAttributesRequest request) {
    assertEquals(6, request.getAttributes().size());
    for (ReplaceableAttribute attr : request.getAttributes()) {
      if (attr.getName().equals(SdbCassandraInstanceDao.ID_KEY)) {
        assertEquals(String.valueOf(ID), attr.getValue());
        assertEquals(false, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.DATACENTER_KEY)) {
        assertEquals(DATACENTER, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.RACK_KEY)) {
        assertEquals(RACK, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.HOSTNAME_KEY)) {
        assertEquals(HOSTNAME, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().endsWith(SdbCassandraInstanceDao.PUBLIC_IP_ADDRESS_KEY)) {
        assertEquals(PUBLIC_IP_ADDRESS, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().endsWith(SdbCassandraInstanceDao.FULLY_QUALIFIED_DOMAIN_NAME_KEY)) {
        assertEquals(FULLY_QUALIFIED_DOMAIN_NAME, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else {
        assertDuplicateAttribute(attr.getName(), attr.getValue());
      }
    }
  }

  // Checkstyle ignore: CyclomaticComplexity
  private static void assertAttributes(DeleteAttributesRequest request) {
    assertEquals(6, request.getAttributes().size());
    for (Attribute attr : request.getAttributes()) {
      if (attr.getName().equals(SdbCassandraInstanceDao.ID_KEY)) {
        assertEquals(String.valueOf(ID), attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.DATACENTER_KEY)) {
        assertEquals(DATACENTER, attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.RACK_KEY)) {
        assertEquals(RACK, attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDao.HOSTNAME_KEY)) {
        assertEquals(HOSTNAME, attr.getValue());
      } else if (attr.getName().endsWith(SdbCassandraInstanceDao.PUBLIC_IP_ADDRESS_KEY)) {
        assertEquals(PUBLIC_IP_ADDRESS, attr.getValue());
      } else if (attr.getName().endsWith(SdbCassandraInstanceDao.FULLY_QUALIFIED_DOMAIN_NAME_KEY)) {
        assertEquals(FULLY_QUALIFIED_DOMAIN_NAME, attr.getValue());
      } else {
        assertDuplicateAttribute(attr.getName(), attr.getValue());
      }
    }
  }

  private static void assertDuplicateAttribute(String name, String value) {
    fail("Duplicate or unexpected attribute: "  + name + "=" + value);
  }

}

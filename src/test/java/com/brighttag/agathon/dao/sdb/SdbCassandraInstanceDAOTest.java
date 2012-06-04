package com.brighttag.agathon.dao.sdb;

import java.math.BigInteger;
import java.util.List;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

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
public class SdbCassandraInstanceDAOTest extends EasyMockSupport {

  private static final int ID = 1;
  private static final BigInteger TOKEN = BigInteger.valueOf(12345);
  private static final String DATACENTER = "dc";
  private static final String RACK = "rack";
  private static final String HOSTNAME = "host";
  private static final String NEXT_TOKEN = "nextToken";

  private AmazonSimpleDBClient simpleDbClient;
  private SdbCassandraInstanceDAO dao;

  @Before
  public void setUp() {
    simpleDbClient = createMock(AmazonSimpleDBClient.class);
    dao = new SdbCassandraInstanceDAO(simpleDbClient);
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

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items);
    expect(result.getNextToken()).andReturn(null);
    replayAll();

    List<CassandraInstance> expected = transform(items);
    assertEquals(expected, dao.findAll());

    assertEquals(SdbCassandraInstanceDAO.ALL_QUERY, requestCapture.getValue().getSelectExpression());
    assertNull(requestCapture.getValue().getNextToken());
  }

  @Test
  public void findAll_paginated() {
    List<Item> items1 = createItems(0);
    List<Item> items2 = createItems(10);
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>(CaptureType.ALL);

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items1);
    expect(result.getNextToken()).andReturn(NEXT_TOKEN);

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andReturn(items2);
    expect(result.getNextToken()).andReturn(null);
    replayAll();

    List<CassandraInstance> expected = Lists.newArrayList(
        Iterables.concat(transform(items1), transform(items2)));
    assertEquals(Ordering.natural().sortedCopy(expected), dao.findAll());

    List<SelectRequest> requests = requestCapture.getValues();
    assertEquals(2, requests.size());
    assertEquals(SdbCassandraInstanceDAO.ALL_QUERY, requests.get(0).getSelectExpression());
    assertNull(requests.get(0).getNextToken());
    assertEquals(SdbCassandraInstanceDAO.ALL_QUERY, requests.get(1).getSelectExpression());
    assertEquals(NEXT_TOKEN, requests.get(1).getNextToken());
  }

  @Test
  public void findById() {
    List<Item> items = ImmutableList.of(createItem(1));
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>();

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andStubReturn(items);
    replayAll();

    List<CassandraInstance> expected = transform(items);
    assertEquals(expected.get(0), dao.findById(ID));

    assertEquals(String.format(SdbCassandraInstanceDAO.INSTANCE_QUERY, ID),
        requestCapture.getValue().getSelectExpression());
    assertNull(requestCapture.getValue().getNextToken());
  }

  @Test
  public void findById_notFound() {
    List<Item> items = ImmutableList.of();
    SelectResult result = createMock(SelectResult.class);
    Capture<SelectRequest> requestCapture = new Capture<SelectRequest>();

    expect(simpleDbClient.select(capture(requestCapture))).andReturn(result);
    expect(result.getItems()).andStubReturn(items);
    replayAll();

    assertNull(dao.findById(ID));
  }

  @Test
  public void save() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    Capture<PutAttributesRequest> requestCapture = new Capture<PutAttributesRequest>();
    expect(instance.getId()).andReturn(ID).times(2);
    expect(instance.getToken()).andStubReturn(TOKEN);
    expect(instance.getDataCenter()).andReturn(DATACENTER);
    expect(instance.getRack()).andReturn(RACK);
    expect(instance.getHostName()).andReturn(HOSTNAME);
    simpleDbClient.putAttributes(capture(requestCapture));
    replayAll();

    dao.save(instance);

    PutAttributesRequest request = requestCapture.getValue();
    assertEquals(SdbCassandraInstanceDAO.DOMAIN, request.getDomainName());
    assertEquals(String.valueOf(ID), request.getItemName());
    assertReplaceableAttributes(request);
  }

  @Test
  public void delete() {
    CassandraInstance instance = createMock(CassandraInstance.class);
    Capture<DeleteAttributesRequest> requestCapture = new Capture<DeleteAttributesRequest>();
    expect(instance.getId()).andReturn(ID).times(2);
    expect(instance.getToken()).andStubReturn(TOKEN);
    expect(instance.getDataCenter()).andReturn(DATACENTER);
    expect(instance.getRack()).andReturn(RACK);
    expect(instance.getHostName()).andReturn(HOSTNAME);
    simpleDbClient.deleteAttributes(capture(requestCapture));
    replayAll();

    dao.delete(instance);

    DeleteAttributesRequest request = requestCapture.getValue();
    assertEquals(SdbCassandraInstanceDAO.DOMAIN, request.getDomainName());
    assertEquals(String.valueOf(ID), request.getItemName());
    assertAttributes(request);
  }

  @Test
  public void transform() {
    Item item = createItem(1);
    replayAll();
    CassandraInstance instance = SdbCassandraInstanceDAO.transform(item);
    assertEquals(1, instance.getId());
    assertEquals("1", instance.getToken().toString());
    assertEquals("dc1", instance.getDataCenter());
    assertEquals("rack1", instance.getRack());
    assertEquals("host1", instance.getHostName());
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
        createAttribute(SdbCassandraInstanceDAO.ID_KEY, String.valueOf(ordinal)),
        createAttribute(SdbCassandraInstanceDAO.TOKEN_KEY, String.valueOf(ordinal)),
        createAttribute(SdbCassandraInstanceDAO.DATACENTER_KEY, DATACENTER + ordinal),
        createAttribute(SdbCassandraInstanceDAO.RACK_KEY, RACK + ordinal),
        createAttribute(SdbCassandraInstanceDAO.HOSTNAME_KEY, HOSTNAME + ordinal));
  }

  private Attribute createAttribute(String key, String value) {
    Attribute attribute = createMock(Attribute.class);
    expect(attribute.getName()).andStubReturn(key);
    expect(attribute.getValue()).andStubReturn(value);
    return attribute;
  }

  private static List<CassandraInstance> transform(List<Item> items) {
    List<CassandraInstance> instances = Lists.newArrayList();
    for (Item item : items) {
      instances.add(SdbCassandraInstanceDAO.transform(item));
    }
    return instances;
  }

  private static void assertReplaceableAttributes(PutAttributesRequest request) {
    assertEquals(5, request.getAttributes().size());
    for (ReplaceableAttribute attr : request.getAttributes()) {
      if (attr.getName().equals(SdbCassandraInstanceDAO.ID_KEY)) {
        assertEquals(String.valueOf(ID), attr.getValue());
        assertEquals(false, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.TOKEN_KEY)) {
        assertEquals(TOKEN.toString(), attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.DATACENTER_KEY)) {
        assertEquals(DATACENTER, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.RACK_KEY)) {
        assertEquals(RACK, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.HOSTNAME_KEY)) {
        assertEquals(HOSTNAME, attr.getValue());
        assertEquals(true, attr.getReplace());
      } else {
        assertDuplicateAttribute(attr.getName(), attr.getValue());
      }
    }
  }

  private static void assertAttributes(DeleteAttributesRequest request) {
    assertEquals(5, request.getAttributes().size());
    for (Attribute attr : request.getAttributes()) {
      if (attr.getName().equals(SdbCassandraInstanceDAO.ID_KEY)) {
        assertEquals(String.valueOf(ID), attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.TOKEN_KEY)) {
        assertEquals(TOKEN.toString(), attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.DATACENTER_KEY)) {
        assertEquals(DATACENTER, attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.RACK_KEY)) {
        assertEquals(RACK, attr.getValue());
      } else if (attr.getName().equals(SdbCassandraInstanceDAO.HOSTNAME_KEY)) {
        assertEquals(HOSTNAME, attr.getValue());
      } else {
        assertDuplicateAttribute(attr.getName(), attr.getValue());
      }
    }
  }

  private static void assertDuplicateAttribute(String name, String value) {
    fail("Duplicate or unexpected attribute: "  + name + "=" + value);
  }

}

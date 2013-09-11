package com.brighttag.agathon.dao.zerg;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class ZergCassandraInstanceDaoTest extends EasyMockSupport {

  private AsyncHttpClient client;
  private ZergCassandraInstanceDao dao;

  @Before
  public void setupMocks() {
    client = createMock(AsyncHttpClient.class);
    dao = new ZergCassandraInstanceDao(client, new Gson(), "/path");
  }

  @Test
  public void findAll() throws Exception {
    expectZergResponse(MANIFEST);
    replayAll();

    assertEquals(INSTANCES, dao.findAll());
  }

  @Test
  public void findAll_badManifest() throws Exception {
    expectZergResponse(BAD_MANIFEST);
    replayAll();

    assertEquals(ImmutableSet.of(), dao.findAll());
  }

  @Test
  public void findById() throws Exception {
    expectZergResponse(MANIFEST);
    replayAll();

    Iterator<CassandraInstance> iterator = INSTANCES.iterator();
    iterator.next();
    assertEquals(iterator.next(), dao.findById(1026494710));
  }

  @Test
  public void findById_notFound() throws Exception {
    expectZergResponse(MANIFEST);
    replayAll();

    assertNull(dao.findById(99));
  }

  @Test
  public void findById_badManifest() throws Exception {
    expectZergResponse(BAD_MANIFEST);
    replayAll();

    assertNull(dao.findById(99));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void save() {
    dao.save(INSTANCES.iterator().next());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    dao.delete(INSTANCES.iterator().next());
  }

  @SuppressWarnings("unchecked")
  private void expectZergResponse(String json) throws Exception {
    BoundRequestBuilder requestBuilder = createMock(BoundRequestBuilder.class);
    ListenableFuture<Response> future = createMock(ListenableFuture.class);
    Response response = createMock(Response.class);
    expect(client.prepareGet("/path")).andReturn(requestBuilder);
    expect(requestBuilder.execute()).andReturn(future);
    expect(future.get()).andReturn(response);
    expect(response.getResponseBody()).andReturn(json);
  }

  private static final Set<CassandraInstance> INSTANCES = ImmutableSet.of(
      new CassandraInstance.Builder().id(1026512133).hostName("cass01we2").publicIpAddress("54.1.1.1")
          .dataCenter("us-west").rack("2a").build(),
      new CassandraInstance.Builder().id(1026494710).hostName("cass01ea1").publicIpAddress("54.2.1.2")
          .dataCenter("us-east").rack("1a").build(),
      new CassandraInstance.Builder().id(1026524501).hostName("cass02ea1").publicIpAddress("54.2.1.3")
          .dataCenter("us-east").rack("1b").build());

  private static final String BAD_MANIFEST = "{]";

  private static final String MANIFEST = "" +
      "{" +
      // Empty region
      "  \"ap-southeast-1\": {}," +
      // Region without Cassandra instances
      "  \"ap-northeast-1\": {" +
      "    \"tagserve01ap1\": {" +
      "      \"private ip\": \"10.0.1.1\"," +
      "      \"roles\": [" +
      "        \"tagserve\"" +
      "      ]," +
      "      \"public ip\": \"54.0.1.1\"," +
      "      \"id\": \"abc1\"," +
      "      \"zone\": \"us-northeast-1a\"" +
      "    }" +
      "  }," +
      // Region with a single Cassandra instance
      "  \"us-west-2\": {" +
      "    \"cass01we2\": {" +
      "      \"private ip\": \"10.1.1.1\"," +
      "      \"roles\": [" +
      "        \"cassandra\"," +
      "        \"priam\"" +
      "      ]," +
      "      \"public ip\": \"54.1.1.1\"," +
      "      \"id\": \"def2\"," +
      "      \"zone\": \"us-west-2a\"" +
      "    }" +
      "  }," +
      // Region with mixed server groups and multiple Cassandra instances
      "  \"us-east-1\": {" +
      "    \"batchfire01ea1\": {" +
      "      \"private ip\": \"10.2.1.1\"," +
      "      \"roles\": [" +
      "        \"batchfire\"," +
      "        \"redis\"" +
      "      ]," +
      "      \"public ip\": \"54.2.1.1\"," +
      "      \"id\": \"ghi3\"," +
      "      \"zone\": \"us-east-1c\"" +
      "    }," +
      "    \"cass01ea1\": {" +
      "      \"private ip\": \"10.2.1.2\"," +
      "      \"roles\": [" +
      "        \"cassandra\"," +
      "        \"priam\"" +
      "      ]," +
      "      \"public ip\": \"54.2.1.2\"," +
      "      \"id\": \"jkl4\"," +
      "      \"zone\": \"us-east-1a\"" +
      "    }," +
      "    \"cass02ea1\": {" +
      "      \"private ip\": \"10.2.1.3\"," +
      "      \"roles\": [" +
      "        \"cassandra\"," +
      "        \"priam\"" +
      "      ]," +
      "      \"public ip\": \"54.2.1.3\"," +
      "      \"id\": \"mno5\"," +
      "      \"zone\": \"us-east-1b\"" +
      "    }" +
      "  }" +
      "}";
}

package com.brighttag.agathon.dao.sdb;

import java.util.List;

import javax.annotation.Nullable;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;

/**
 * SimpleDB implementation of {@link CassandraInstanceDAO}.
 * <br/>
 * All methods throw AmazonClientException and AmazonServiceExceptions at runtime.
 * TODO: catch, wrap, and rethrow these as less Amazon-y exceptions.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class SdbCassandraInstanceDAO implements CassandraInstanceDAO {

  @VisibleForTesting static final String DOMAIN = "CassandraInstances";

  @VisibleForTesting static final String ID_KEY = "id";
  @VisibleForTesting static final String TOKEN_KEY = "token";
  @VisibleForTesting static final String DATACENTER_KEY = "datacenter";
  @VisibleForTesting static final String RACK_KEY = "rack";
  @VisibleForTesting static final String HOSTNAME_KEY = "hostname";

  @VisibleForTesting static final String ALL_QUERY =
      "SELECT * FROM " + DOMAIN + " WHERE " + TOKEN_KEY + " is not null ORDER BY " + TOKEN_KEY;
  @VisibleForTesting static final String INSTANCE_QUERY =
      "SELECT * FROM " + DOMAIN + " WHERE " + ID_KEY + " = '%s' LIMIT 1";

  private final AmazonSimpleDBClient client;

  @Inject
  public SdbCassandraInstanceDAO(AmazonSimpleDBClient client) {
    this.client = client;
  }

  @Override
  public List<CassandraInstance> findAll() {
    ImmutableList.Builder<CassandraInstance> instances = ImmutableList.builder();
    String nextToken = null;

    do {
      SelectRequest request = new SelectRequest(ALL_QUERY).withNextToken(nextToken);
      SelectResult result = client.select(request);

      for (Item item : result.getItems()) {
        instances.add(transform(item));
      }

      nextToken = result.getNextToken();
    } while (nextToken != null);

    return instances.build();
  }

  @Override
  public @Nullable CassandraInstance findById(String id) {
    SelectRequest request = new SelectRequest(String.format(INSTANCE_QUERY, id));
    SelectResult result = client.select(request);

    if (result.getItems().size() == 0) {
      return null;
    }

    return transform(result.getItems().get(0));
  }

  @Override
  public void save(CassandraInstance instance) {
    PutAttributesRequest request = new PutAttributesRequest(
        DOMAIN, instance.getId(), buildSaveAttributes(instance));
    client.putAttributes(request);
  }

  @Override
  public void delete(CassandraInstance instance) {
    DeleteAttributesRequest request = new DeleteAttributesRequest(
        DOMAIN, instance.getId(), buildDeleteAttributes(instance));
    client.deleteAttributes(request);
  }

  @VisibleForTesting static CassandraInstance transform(Item item) {
    CassandraInstance.Builder instanceBuilder = new CassandraInstance.Builder();
    for (Attribute attr : item.getAttributes()) {
      if (attr.getName().equals(ID_KEY)) {
        instanceBuilder.id(attr.getValue());
      } else if (attr.getName().equals(TOKEN_KEY)) {
        instanceBuilder.token(attr.getValue());
      } else if (attr.getName().equals(DATACENTER_KEY)) {
        instanceBuilder.dataCenter(attr.getValue());
      } else if (attr.getName().equals(RACK_KEY)) {
        instanceBuilder.rack(attr.getValue());
      } else if (attr.getName().equals(HOSTNAME_KEY)) {
        instanceBuilder.hostName(attr.getValue());
      }
    }
    return instanceBuilder.build();
  }

  private static List<ReplaceableAttribute> buildSaveAttributes(CassandraInstance instance) {
    List<ReplaceableAttribute> attrs = Lists.newArrayList();
    attrs.add(attribute(ID_KEY, instance.getId(), false));
    attrs.add(attribute(TOKEN_KEY, instance.getToken(), true));
    attrs.add(attribute(DATACENTER_KEY, instance.getDataCenter(), true));
    attrs.add(attribute(RACK_KEY, instance.getRack(), true));
    attrs.add(attribute(HOSTNAME_KEY, instance.getHostName(), true));
    return attrs;
  }

  private static List<Attribute> buildDeleteAttributes(CassandraInstance instance) {
    List<Attribute> attrs = Lists.newArrayList();
    attrs.add(attribute(ID_KEY, instance.getId()));
    attrs.add(attribute(TOKEN_KEY, instance.getToken()));
    attrs.add(attribute(DATACENTER_KEY, instance.getDataCenter()));
    attrs.add(attribute(RACK_KEY, instance.getRack()));
    attrs.add(attribute(HOSTNAME_KEY, instance.getHostName()));
    return attrs;
  }

  private static Attribute attribute(String key, String value) {
    return new Attribute(key, value);
  }

  private static ReplaceableAttribute attribute(String key, String value, boolean replaceable) {
    return new ReplaceableAttribute(key, value, replaceable);
  }

}

require 'smokestrap'

# Implementation notes:
#
# These tests depend on a `sleep` call in `agathon_setup` to ensure SimpleDB propagates
# the initial creates before we run any queries. Unfortunately, we're not so lucky with
# the deletion test;  it sporadically fails with a 404 with even a long sleep time, so
# its commented out for now. Hopefully, if/when we move away from SimpleDB, we can uncomment
# this test, remove the `sleep` call, and the tests will run faster.

context "[agathon:CassandraInstance]" do
  agathon_setup

  content_type = {"Content-Type" => "application/json"}

  context "Get all instances" do
    base_uri BrightTag.agathon_host
    get "/rings/UserStats/instances"
    asserts_status.equals(200)
    asserts { response.size }.equals(12)

    asserts_json("[1].rack").equals("1a")
    asserts_json("[1].hostName").equals("cass01eu1")
    asserts_json("[1].dataCenter").equals("eu-west")
    asserts_json("[1].publicIpAddress").equals("2.2.2.2")

    asserts_json("[3].rack").equals("1b")
    asserts_json("[3].hostName").equals("cass02ea1")
    asserts_json("[3].dataCenter").equals("us-east")
    asserts_json("[3].publicIpAddress").equals("4.4.4.4")
  end

  context "Get non-existent instance returns 404" do
    base_uri BrightTag.agathon_host
    get "/rings/UserStats/instances/111"
    asserts_status.equals(404)
  end

  context "Create instance, unrecognized Content-Type returns 415" do
    base_uri BrightTag.agathon_host
    post "/rings/UserStats/instances", :body => {}.to_json
    asserts_status.equals(415)
  end

  context "Create instance, unparseable JSON returns 400" do
    base_uri BrightTag.agathon_host
    post "/rings/UserStats/instances", :headers => content_type, :body => '{"id": "forgot-to-close-me}'
    asserts_status.equals(400)
  end

  context "Create instance" do
    base_uri BrightTag.agathon_host
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass10ea1", "publicIpAddress" => "1.2.3.4" }
    post "/rings/UserStats/instances", :headers => content_type, :body => instance.to_json
    asserts_status.equals(201)
  end

  # context "Delete instance" do
  #   base_uri BrightTag.agathon_host
  #   instance = { "id" => "444", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass444ea1", "publicIpAddress" => "1.2.3.4" }
  #   post "/instances", :headers => content_type, :body => instance.to_json
  #   delete "/instances/444", :headers => content_type
  #   asserts_status.equals(204)
  # end

  #
  # Validation Tests
  #

  context "Create instance, missing 'id' returns 422" do
    instance = { "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'datacenter' returns 422" do
    instance = { "id" => "222", "rack" => "1a", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'rack' returns 422" do
    instance = { "id" => "222", "datacenter" => "us-east", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'hostname' returns 422" do
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "1a", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'id' returns 422" do
    instance = { "id" => "", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'datacenter' returns 422" do
    instance = { "id" => "222", "datacenter" => "", "rack" => "1a", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'rack' returns 422" do
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "", "hostname" => "cass01ea1", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'hostname' returns 422" do
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "1a", "hostname" => "", "publicIpAddress" => "1.2.3.4" }
    assert_invalid_instance(instance, content_type)
  end

end

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
    get "/instances"
    asserts_status.equals(200)
    asserts { response.size }.equals(12)

    # test that they're ordered by token
    asserts_json("[0].token").equals("0")
    asserts_json("[1].token").equals("1")
    asserts_json("[2].token").equals("2")
    asserts_json("[3].token").equals("42535295865117307932921825928971026432")
    asserts_json("[4].token").equals("42535295865117307932921825928971026433")
    asserts_json("[5].token").equals("42535295865117307932921825928971026434")
    asserts_json("[6].token").equals("85070591730234615865843651857942052864")
    asserts_json("[7].token").equals("85070591730234615865843651857942052865")
    asserts_json("[8].token").equals("85070591730234615865843651857942052866")
    asserts_json("[9].token").equals("127605887595351923798765477786913079296")
    asserts_json("[10].token").equals("127605887595351923798765477786913079297")
    asserts_json("[11].token").equals("127605887595351923798765477786913079298")

    asserts_json("[1].token").equals("1")
    asserts_json("[1].rack").equals("1a")
    asserts_json("[1].hostName").equals("cass01eu1")
    asserts_json("[1].dataCenter").equals("eu-west")

    asserts_json("[3].token").equals("42535295865117307932921825928971026432")
    asserts_json("[3].rack").equals("1b")
    asserts_json("[3].hostName").equals("cass02ea1")
    asserts_json("[3].dataCenter").equals("us-east")
  end

  context "Get non-existent instance returns 404" do
    base_uri BrightTag.agathon_host
    get "/instances/111"
    asserts_status.equals(404)
  end

  context "Create instance, unrecognized Content-Type returns 415" do
    base_uri BrightTag.agathon_host
    post "/instances", :body => {}.to_json
    asserts_status.equals(415)
  end

  context "Create instance, unparseable JSON returns 400" do
    base_uri BrightTag.agathon_host
    post "/instances", :headers => content_type, :body => '{"id": "forgot-to-close-me}'
    asserts_status.equals(400)
  end

  context "Create instance" do
    base_uri BrightTag.agathon_host
    instance = { "id" => "222", "token" => 2**127, "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass10ea1" }
    post "/instances", :headers => content_type, :body => instance.to_json
    asserts_status.equals(201)
  end

  # context "Delete instance" do
  #   base_uri BrightTag.agathon_host
  #   instance = { "id" => "444", "datacenter" => "us-east", "rack" => "1a", "token" => 1234, "hostname" => "cass444ea1" }
  #   post "/instances", :headers => content_type, :body => instance.to_json
  #   delete "/instances/444", :headers => content_type
  #   asserts_status.equals(204)
  # end

  #
  # Validation Tests
  #

  context "Create instance, missing 'id' returns 422" do
    instance = { "token" => 1234, "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'token' returns 422" do
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'datacenter' returns 422" do
    instance = { "id" => "222", "token" => 1234, "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'rack' returns 422" do
    instance = { "id" => "222", "token" => 1234, "datacenter" => "us-east", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, missing 'hostname' returns 422" do
    instance = { "id" => "222", "token" => 1234, "datacenter" => "us-east", "rack" => "1a" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'id' returns 422" do
    instance = { "id" => "", "token" => 1234, "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'token' returns 422" do
    instance = { "id" => "222", "token" => "", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'datacenter' returns 422" do
    instance = { "id" => "222", "token" => 1234, "datacenter" => "", "rack" => "1a", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'rack' returns 422" do
    instance = { "id" => "222", "token" => 1234, "datacenter" => "us-east", "rack" => "", "hostname" => "cass01ea1" }
    assert_invalid_instance(instance, content_type)
  end

  context "Create instance, empty 'hostname' returns 422" do
    instance = { "id" => "222", "token" => 1234, "datacenter" => "us-east", "rack" => "1a", "hostname" => "" }
    assert_invalid_instance(instance, content_type)
  end

end

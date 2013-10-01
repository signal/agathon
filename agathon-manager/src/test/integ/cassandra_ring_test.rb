require 'smokestrap'

# Implementation notes:
#
# These tests depend on a `sleep` call in `agathon_setup` to ensure SimpleDB propagates
# the initial creates before we run any queries. Unfortunately, we're not so lucky with
# the deletion test;  it sporadically fails with a 404 with even a long sleep time, so
# its commented out for now. Hopefully, if/when we move away from SimpleDB, we can uncomment
# this test, remove the `sleep` call, and the tests will run faster.

context "[agathon:CassandraRing]" do
  agathon_setup

  content_type = {"Content-Type" => "application/json"}

  context "Get all rings" do
    base_uri BrightTag.agathon_host
    get "/rings"
    asserts_status.equals(200)
    asserts { response.size }.equals(1)

    asserts_json("[0].name").equals("UserStats")
    asserts_json("[0].instances").size(13) # 12 seeds + 1 created in test
  end

  context "Get non-existent ring returns 404" do
    base_uri BrightTag.agathon_host
    get "/rings/NoRingHere"
    asserts_status.equals(404)
  end

  context "Create ring, unrecognized Content-Type returns 415" do
    base_uri BrightTag.agathon_host
    post "/rings", :body => {}.to_json
    asserts_status.equals(415)
  end

  context "Create ring, unparseable JSON returns 400" do
    base_uri BrightTag.agathon_host
    post "/rings", :headers => content_type, :body => '{"name": "forgot-to-close-me}'
    asserts_status.equals(400)
  end

  context "Create ring" do
    base_uri BrightTag.agathon_host
    instance = { "id" => "222", "datacenter" => "us-east", "rack" => "1a", "hostname" => "cass10ea1", "publicIpAddress" => "1.2.3.4" }
    ring = { "name" => "IntegrationRing", "instances" => [instance] }
    post "/rings", :headers => content_type, :body => ring.to_json
    asserts_status.equals(201)
  end

  context "Delete ring" do
    base_uri BrightTag.agathon_host
    ring = { "name" => "IntegrationRing", "instances" => [] }
    post "/rings", :headers => content_type, :body => ring.to_json
    delete "/rings/IntegrationRing", :headers => content_type
    asserts_status.equals(204)
  end

  #
  # Validation Tests
  #

  context "Create ring, missing 'name' returns 422" do
    base_uri BrightTag.agathon_host
    ring = { "instances" => [] }
    post "/rings", :headers => content_type, :body => ring.to_json
    asserts_status.equals(422)
  end

  # Ideally 422 - but instantiation problem without instances and don't care to fix
  context "Create ring, missing 'instances' returns 400" do
    base_uri BrightTag.agathon_host
    ring = { "name" => "IntegrationRing" }
    post "/rings", :headers => content_type, :body => ring.to_json
    asserts_status.equals(400)
  end

end

require 'smokestrap'

context "[agathon:Seeds]" do
  agathon_setup

  content_type = {"Content-Type" => "application/json"}

  context "Get seeds" do
    base_uri BrightTag.agathon_host
    get "/seeds"
    asserts_status.equals(200)
    asserts("body") { response.body }.equals("cass01ea1,cass02ea1,cass01eu1,cass02eu1,cass01we1,cass02we1")
  end

end

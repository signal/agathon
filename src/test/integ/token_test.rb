require 'smokestrap'

context "[agathon:Token]" do
  agathon_setup

  content_type = {"Content-Type" => "application/json"}

  context "Get token" do
    base_uri BrightTag.agathon_host
    get "/token"
    asserts_status.equals(200)
    asserts("body") { response.body }.equals("127605887595351923798765477786913079296")
  end

end
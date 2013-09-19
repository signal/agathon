Riot::Context.class_eval do

  def assert_invalid_instance(instance, headers)
    base_uri BrightTag.agathon_host
    post "/rings/UserStats/instances", :headers => headers, :body => instance.to_json
    asserts_status.equals(422)
  end

end

require 'logger'
require 'yaml'

module SmokeMonster
  module Riot
    module BrightTag

      def setup_seeds(domain_namespace, seed_file)
        domain = "CassandraInstances.#{domain_namespace}.#{seed_file}"
        seedql = YAML.load_file(Pathname(__FILE__).dirname + "#{seed_file}.yaml")
        setup_sdb_seeds(domain, seedql)
        domain = "CassandraInstances.#{domain_namespace}.IntegrationRing" # created by test
        AwsSdb::Service.new(:logger => Logger.new('logs/aws_sdb.log')).delete_domain(domain)
        sleep(2) # buy some time for SimpleDB propagation
      end

    private

      def setup_sdb_seeds(domain, seedql)
        sdb = AwsSdb::Service.new(:logger => Logger.new('logs/aws_sdb.log'))
        truncate_sdb(sdb, domain)
        seed_sdb(sdb, domain, seedql)
      end

      def truncate_sdb(sdb, domain)
        sdb.delete_domain(domain)
        sdb.create_domain(domain)
      end

      def seed_sdb(sdb, domain, seedql)
        seedql.each do |key, value|
          sdb.put_attributes(domain, key, {"id" => key}.merge(value))
        end
      end

    end # BrightTag
  end # Riot
end # SmokeMonster

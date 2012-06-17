require 'logger'
require 'yaml'

module SmokeMonster
  module Riot
    module BrightTag

      def setup_seeds(domain, seed_file)
        seedql = YAML.load_file(Pathname(__FILE__).dirname + "#{seed_file}.yaml")
        setup_sdb_seeds(domain, seedql)
        sleep(2) # buy some time for SimpleDB propagation
      end

    private

      def setup_sdb_seeds(domain, seedql)
        sdb = AwsSdb::Service.new(:logger => Logger.new('logs/aws_sdb.log'))
        drop_sdb(sdb, domain)
        seed_sdb(sdb, domain, seedql)
      end

      def drop_sdb(sdb, domain)
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

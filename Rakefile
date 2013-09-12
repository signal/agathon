require 'rubygems'

require 'rake'
require 'rake/testtask'

desc "Run all the integration tests"
task :test do
  ENV["AGATHON_HOST"] ||= "http://localhost:8094"
  ENV["AGATHON_SDB_DOMAIN"] ||= "CassandraInstances"
  puts "Agathon @  \e[36m#{ENV["AGATHON_HOST"]}\e[0m"
  Rake::TestTask.new do |t|
    t.libs << "src/test/integ"
    t.pattern = "src/test/integ/**/*_test.rb"
    t.verbose = false
  end
end

task :default => :test

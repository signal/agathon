require 'rubygems'
require 'bundler'
Bundler.require

require 'setup_helpers'
require 'assertion_extensions'

Riot.dots
Riot.plain! if ENV['RIOT_PLAIN']

module BrightTag
  def self.agathon_host; ENV["AGATHON_HOST"]; end
end

module SmokeMonster::Riot::BrightTag
  def agathon_setup
    setup_seeds(ENV["AGATHON_SDB_DOMAIN"], "agathon")
  end
end

Riot::Context.instance_eval do
  include SmokeMonster::Riot::BrightTag
end

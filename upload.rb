#! /usr/bin/env ruby
#
# ruby upload.rb user pass user/repo file '(description)'
#

require "rubygems"
require 'json'

if ARGV.size < 4
  puts "\nUSAGE: upload.rb [user] [pass] [user/repo] [filepath] ('description')"
  exit
end

user = ARGV[0]
pass = ARGV[1]
repo = ARGV[2]
file = ARGV[3]
desc = ARGV[4] rescue ''

def url(path)
  "https://api.github.com#{path}"
end

size = File.size(file)
fname = File.basename(file)

# create entry
args = 
data = `curl -s -XPOST -d '{"name":"#{fname}","size":#{size},"description":"#{desc}"}' -u "#{user}:#{pass}" #{url("/repos/#{repo}/downloads")}`
data = JSON.parse(data)

# upload file to bucket
cmd  = "curl -s "
cmd += "-F \"key=#{data['path']}\" "
cmd += "-F \"acl=#{data['acl']}\" "
cmd += "-F \"success_action_status=201\" "
cmd += "-F \"Filename=#{data['name']}\" "
cmd += "-F \"AWSAccessKeyId=#{data['accesskeyid']}\" "
cmd += "-F \"Policy=#{data['policy']}\" "
cmd += "-F \"Signature=#{data['signature']}\" "
cmd += "-F \"Content-Type=#{data['mime_type']}\" "
cmd += "-F \"file=@#{file}\" "
cmd += "https://github.s3.amazonaws.com/"

xml = `#{cmd}`

if m = /\<Location\>(.*)\<\/Location\>/.match(xml)
  puts "Your file is uploaded to:"
  puts m[1].gsub('%2F', '/')  # not sure i want to fully URL decode this, but these will not do
else
  puts "Upload failed. Response is:\n\n #{xml}"
end

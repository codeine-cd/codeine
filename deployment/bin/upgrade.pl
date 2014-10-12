#!/usr/bin/perl

use warnings;
use strict;
use Cwd 'abs_path';
use File::Basename;
use Getopt::Long;

my $script = __FILE__;
my $dir = dirname($script);
chdir $dir."/../../";
my $baseDir = `pwd`;
chomp $baseDir;
print "baseDir is $baseDir\n";
my $version = undef;

GetOptions(
	"version=s" => \$version,
);


system("wget -O codeine.zip --no-check-certificate -e use_proxy=yes -e https_proxy=proxy.iil.intel.com:911 https://github.com/codeine-cd/codeine/releases/download/$version/codeine.zip");
system("rm -rf $baseDir/deployment_prev");
system("mv $baseDir/deployment $baseDir/deployment_prev");
system("mkdir $baseDir/deployment");
system("cd $baseDir/deployment ; unzip ../codeine.zip");
system("$dir/run-server.pl --kill --daemon &");
#!/usr/bin/perl

use warnings;
use strict;
use Cwd 'abs_path';
use File::Basename;
use Getopt::Long qw(:config pass_through);

my $script = __FILE__;
my $dir = dirname($script);
my $baseDir = $dir."../../";
system("rm -rf $baseDir/deployment_prev");
system("mv $baseDir/deployment $baseDir/deployment_prev");
chdir $dir;
system("$dir/run-server.pl --kill --daemon &");
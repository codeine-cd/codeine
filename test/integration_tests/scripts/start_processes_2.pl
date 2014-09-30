#!/usr/bin/perl
use strict;
use FindBin;
use File::Basename;

my $script = __FILE__;
my $dir = dirname($script);
chdir $dir || die "cant cd to $dir";
$dir = `pwd`;
chomp($dir);
print "dir is $dir\n";
my $deployment = "$ARGV[0]";
my $mysql_dir = "$ARGV[1]";
my $httpRoot = "$deployment/http-root";

#my $grepCmd = "ps -efww | grep codeine | grep -E 'mysql|codeine-server.jar' | grep -v grep | grep -v start_processes.pl | grep -v junit";
#print "grep:\n";
#system($grepCmd);
#system("kill `$grepCmd | awk '{print \$2}' | tr '\n' ' '`");

#sleep 5;

#print "grep after kill:\n";
#system($grepCmd);
$ENV{'JAVA_HOME'} = "/usr/intel/pkgs/java/1.7.0.25";
e("rm -rf $httpRoot/*.log");
e("rm -rf $mysql_dir");
e("mkdir -p $mysql_dir");
e("$deployment/bin/run-server.pl --kill --daemon");
e("$deployment/bin/run-peer.pl --kill --daemon");
print "done!\n";

sub e()
{
	my $cmd = shift;
	print "executing $cmd\n";
	system($cmd);
}

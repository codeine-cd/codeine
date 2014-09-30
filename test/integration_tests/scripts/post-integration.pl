#!/usr/bin/perl
use strict;
use FindBin;
use File::Basename;


print "killing existing peer\n";
my $grepCmd = "ps -efww | grep codeine.CodeinePeerBootstrap | grep -v grep";
print "grep:\n";
system($grepCmd);
system("kill `$grepCmd | awk '{print \$2}' | tr '\n' ' '`");
print "killing existing servers\n";
my $grepCmd = "ps -efww | grep codeine.CodeineServerBootstrap | grep -v grep";
print "grep:\n";
system($grepCmd);
system("kill `$grepCmd | awk '{print \$2}' | tr '\n' ' '`");
my $workdir = `cat codeine.integration.conf`;
chomp($workdir);
e("rm -rf $workdir");
e("rm -rf codeine.integration.conf.json");
e("rm -rf codeine.integration.conf");
print "done!\n";

sub e()
{
	my $cmd = shift;
	print "executing $cmd\n";
	system($cmd);
}

#!/usr/bin/perl
use strict;
use FindBin;
use File::Basename;


my $workdir = "/tmp/codeine_workdir_$$";
e("mkdir $workdir");
e("echo '{\n\"work_dir\": \"$workdir\"\n}' > codeine.integration.conf.json");
e("echo $workdir > codeine.integration.conf");
my $tar = "codeine.latest.tar.gz";
my $dir = $workdir."/deployment";


e("rm -rf $dir");
e("mkdir -p $dir");
e("tar -xvzf $tar --directory=$dir");
print "done!\n";

sub e()
{
	my $cmd = shift;
	print "executing $cmd\n";
	system($cmd);
}

#!/usr/bin/perl
use strict;
use FindBin;
use File::Basename;

my $tar = "$ARGV[0]";
my $dir = "$ARGV[1]";

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

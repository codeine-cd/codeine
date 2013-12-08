#!/usr/bin/perl
use strict;
use FindBin;

if (-e "/tmp/fail")
{
	print "error: file /tmp/fail exists\n";
	exit 1;
}
exit 0;
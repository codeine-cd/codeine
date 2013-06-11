#!/usr/bin/perl
#

use warnings;
use strict;

my $propertiesFile = "src/common/yami/version.properties";
my $repo = "https://github.com/oshai/yami.git";

print "update version...\n";
my $major = getVersion('major');
my $minor = getVersion('minor');
my $build = getVersion('build');
my $date = getVersion('date');
my $version = "$major.$minor.$build.$date";
print "version is $version\n";	
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime time;
$build++;
$year += 1900;
$mon  += 1;
$mon = sprintf( "%02d", $mon );
$mday = sprintf( "%02d", $mday );
$date = $year.$mon.$mday;
$version = "$major.$minor.$build.$date";
print "new version is $version\n";
system("echo 'YamiVersion.build=$build\nYamiVersion.major=$major\nYamiVersion.minor=$minor\nYamiVersion.date=$date' > $propertiesFile");
system("git commit -m \"bounce version to $version\" $propertiesFile");
system("git push");

sub getVersion
{
	my $key = shift;
	my $value = `cat $propertiesFile | grep $key | awk -F= '{print \$2}'`;
	chomp($value);
	return $value;
}

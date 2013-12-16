#!/usr/bin/perl
#

use warnings;
use strict;
use File::Basename;
use Getopt::Long qw(:config pass_through);

my $script = __FILE__;
my $dir = dirname($script);
my $codeineDir = "$dir";
my $propertiesFile = "$codeineDir/src/common/codeine/version.properties";

my $date_only = undef;

GetOptions(
	"date-only"  => \$date_only,
);

print "update version...\n";
my $major = getVersion('major');
my $minor = getVersion('minor');
my $build = getVersion('build');
my $date = getVersion('date');
my $version = "$major.$minor.$build.$date";
print "version is $version\n";	
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime time;
$build++ unless $date_only;
$year += 1900;
$mon  += 1;
$mon = sprintf( "%02d", $mon );
$mday = sprintf( "%02d", $mday );
$date = $year.$mon.$mday;
$version = "$major.$minor.$build.$date";
print "new version is $version\n";
system("echo 'CodeineVersion.build=$build\nCodeineVersion.major=$major\nCodeineVersion.minor=$minor\nCodeineVersion.date=$date' > $propertiesFile");

sub getVersion
{
	my $key = shift;
	my $value = `cat $propertiesFile | grep $key | awk -F= '{print \$2}'`;
	chomp($value);
	return $value;
}

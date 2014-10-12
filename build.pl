#!/usr/bin/perl

use warnings;
use strict;
use File::Basename;


print "prepare codeine...\n";
my $propertiesFile = "src/common/codeine/version.properties";
updateVersionFile();
es("rm -rf dist");
es("mkdir -p dist");
#es("bounce_minor_version.pl");

my $version = getVersionFull();
my $versionNoDate = getVersionNoDate();
print "mvn is $ENV{mvn}\n";
es("$ENV{mvn} clean verify");
my $tar = "codeine_".getVersionNoDate().".tar.gz";
my $zip = "codeine_".getVersionNoDate().".zip";

if (defined($ENV{RELEASE_AREA}))
{
	my $link = $ENV{RELEASE_AREA}."/codeine.latest.tar";
	es("cp $tar $ENV{RELEASE_AREA}/");
	system("rm $link");
	es("ln -s $tar $link");
}
unless ($ENV{'release-to-github'} eq "true") 
{
	exit(0);
} 
print "Will release new version to Github: $versionNoDate\n";
my $githubUser = $ENV{GITHUB_USER};
my $githubPassword = $ENV{GITHUB_PASSWORD};
my $res = r("curl -X POST -u $githubUser:$githubPassword -H \"Content-Type: application/json\" -d '{  \"tag_name\": \"v$versionNoDate\",  \"target_commitish\": \"master\",  \"name\": \"v$versionNoDate\",  \"body\": \"Codeine Release\",  \"draft\": false,  \"prerelease\": true}' https://api.github.com/repos/codeine-cd/codeine/releases");
print "release returned: $res\n";
$res =~ /\"id\":\s([^,]*)/;
my $id = $1;
print "release id: $id\n";
es("curl -X POST -u $githubUser:$githubPassword -H \"Content-Type: application/zip\" --data-binary \"\@$zip\" \"https://uploads.github.com/repos/codeine-cd/codeine/releases/$id/assets?name=codeine.zip\"");

print "Done!";

sub es
{
	e(shift);
	my $status = $?;
	if ($status != 0)
	{
		print "error on execution, will exit.\n";
		exit $status >> 8;
	}
}
sub e
{
	my $cmd = shift;
	print "executing $cmd\n";
	system($cmd);
}
sub r
{
	my $cmd = shift;
	print "executing $cmd\n";
	return `$cmd`;
}
sub updateVersionFile
{
	my $major = getVersion('major');
	my $minor = getVersion('minor');
	my $build = $ENV{BUILD_NUMBER};
	my $date = getDate();
	es("echo 'CodeineVersion.build=$build\nCodeineVersion.major=$major\nCodeineVersion.minor=$minor\n' > $propertiesFile");
}
sub getVersionFull
{
	my $major = getVersion('major');
	my $minor = getVersion('minor');
	my $build = $ENV{BUILD_NUMBER} || getVersion('build');
	my $date = getDate();
	my $version = "$major.$minor.$build";	
	return $version;
}
sub getVersionNoDate
{
	my $major = getVersion('major');
	my $minor = getVersion('minor');
	my $build = $ENV{BUILD_NUMBER} || getVersion('build');
	my $version = "$major.$minor.$build";	
	return $version;
}
sub getVersion
{
	my $key = shift;
	my $value = `cat $propertiesFile | grep $key | awk -F= '{print \$2}'`;
	chomp($value);
	return $value;
}

sub getDate
{
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime time;
	$year += 1900;
	$mon  += 1;
	$mon = sprintf( "%02d", $mon );
	$mday = sprintf( "%02d", $mday );
	my $date = $year.$mon.$mday;
	return $date;
}



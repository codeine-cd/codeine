#!/usr/bin/perl

use warnings;
use strict;
use File::Basename;


print "prepare codeine...\n";
my $propertiesFile = "src/common/codeine/version.properties";
updateVersionFile();
#es("rm -rf dist");
es("mkdir -p dist");
#es("bounce_minor_version.pl");

my $version = getVersionFull();
my $versionNoDate = getVersionNoDate();
print "java is $ENV{JAVA_HOME}\n";#1.7
print "ant is ant\n";#1.8?
es("ant", 1);
es("cd deployment/http-root/ajs ; npm install");
es("cd deployment/http-root/ajs ; bower install");
es("cd deployment/http-root/ajs ; grunt");
es("rsync -ur deployment/bin dist/");
es("rsync -ur deployment/conf dist/");
es("rsync -ur deployment/project dist/");
es("mkdir -p dist/http-root/ajs");
es("cp -r deployment/http-root/ajs/dist dist/http-root/ajs/dist");
es("echo '".getVersionNoDate()."' > dist/build_number.txt");
my $tar = "codeine_".getVersionNoDate().".tar.gz";
es("cd dist; tar -czf ../$tar ./*");
print "tar is ready '$tar' for version $version\n";
my $zip = "codeine_".getVersionNoDate().".zip";
es("cd dist; zip -r ../$zip ./*");
print "zip is ready '$zip' for version $version\n";

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
my $res = r("curl -X POST -u $githubUser:$githubPassword -H \"Content-Type: application/json\" -d '{  \"tag_name\": \"v$versionNoDate\",  \"target_commitish\": \"master\",  \"name\": \"v$versionNoDate\",  \"body\": \"Codeine Release\",  \"draft\": false,  \"prerelease\": true}' https://api.github.com/repos/yami-cd/yami/releases");
print "release returned: $res\n";
$res =~ /\"id\":\s([^,]*)/;
my $id = $1;
print "release id: $id\n";
es("curl -X POST -u $githubUser:$githubPassword -H \"Content-Type: application/zip\" --data-binary \"\@$zip\" \"https://uploads.github.com/repos/yami-cd/yami/releases/$id/assets?name=codeine.zip\"");

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



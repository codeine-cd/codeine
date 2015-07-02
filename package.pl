#!/usr/bin/perl


use warnings;
use strict;
use File::Basename;


print "prepare codeine...\n";
my $propertiesFile = "src/common/codeine/version.properties";

my $version = getVersionFull();
my $versionNoDate = getVersionNoDate();
es("mkdir -p dist/bin");
es("cp target/codeine-1.0.0-jar-with-dependencies.jar dist/bin/codeine.jar");
es("cd deployment/http-root/ajs ; npm install");
es("rm -rf deployment/http-root/ajs/app/bower_components");
es("cd deployment/http-root/ajs ; bower install");
if (defined($ENV{INTEL_INSIDE}))
{
    es("cd deployment/http-root/ajs; /bin/cp /nfs/iil/gen/itec/sws3/dist/workspace/misc/web/css-url-rewriter.js node_modules/grunt-cdnify/node_modules/css-url-rewriter/lib/css-url-rewriter.js");
}
es("cd deployment/http-root/ajs ; grunt");
es("mkdir -p dist/http-root/ajs");
es("rsync -ur deployment/http-root/ajs/dist dist/http-root/ajs/");
es("rsync -ur deployment/bin dist/");
es("rsync -ur deployment/bin_windows dist/");
es("rsync -ur deployment/conf dist/");
es("rsync -ur deployment/project dist/");
es("echo '".getVersionNoDate()."' > dist/build_number.txt");
my $tar = "codeine_".getVersionNoDate().".tar.gz";
es("cd dist; tar -czf ../$tar ./*");
print "tar is ready '$tar' for version $version\n";
es("cp $tar codeine.latest.tar.gz");
my $zip = "codeine_".getVersionNoDate().".zip";
es("cd dist; zip -r ../$zip ./*");
print "zip is ready '$zip' for version $version\n";
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



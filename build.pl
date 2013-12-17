#!/usr/bin/perl

use warnings;
use strict;
use File::Basename;

my $script = __FILE__;
my $dir = dirname($script);
chdir $dir || die "cant cd to $dir";
$dir = `pwd`;
chomp($dir);
print "dir is $dir\n";
my $codeineDir = "$dir";
print "codeineDir is $codeineDir\n";
print "prepare codeine...\n";
my $propertiesFile = "$codeineDir/src/common/codeine/version.properties";
es("rm -rf dist");
es("mkdir -p dist");
#es("bounce_minor_version.pl");

my $version = getVersionFull();
print "java is $ENV{JAVA_HOME}\n";#1.7
print "ant is ant\n";#1.8?
es("cd $codeineDir ; ant", 1);
es("rsync -ur $codeineDir/deployment/* dist/");
#es("rsync -ur deployment/bin/* dist/bin/");
#es("cp $codeineDir/dist/bin/codeine.jar dist/bin/codeine.jar");
#es("rsync -ur deployment/project dist/");
#es("rsync -ur deployment/conf dist/");
es("grep \"CodeineVersion.build=\" $propertiesFile | awk -F= '{print \$2}' > dist/build_number.txt");
my $tar = "codeine_".getVersionNoDate().".tar.gz";
es("cd dist; tar -czf ../$tar ./*");
print "tar is ready '$tar' for version $version\n";


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
sub getVersionFull
{
	my $major = getVersion('major');
	my $minor = getVersion('minor');
	my $build = $ENV{BUILD_NUMBER} || getVersion('build');
	my $date = getDate();
	my $version = "$major.$minor.$build.$date";	
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



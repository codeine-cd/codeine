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
print "deploy codeine...\n";
my $propertiesFile = "$codeineDir/src/common/codeine/version.properties";
my $deployDir = "$ARGV[0]";
print "deploy dir: $deployDir\n";
exit(1) unless (-d $deployDir);
my $version = getVersionFull();
my $tar = "codeine_".getVersionNoDate().".tar.gz";
print "deploying to $deployDir/$tar\n";
es("cp $tar $deployDir/$tar");
es("cp $tar codeine.latest.tar.gz");
es("rm $deployDir/codeine.latest.tar.gz");
es("ln -s $tar $deployDir/codeine.latest.tar.gz");
print "deployed\n";


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
	my $build = getVersion('build');
	my $date = getVersion('date');
	my $version = "$major.$minor.$build.$date";	
	return $version;
}
sub getVersionNoDate
{
	my $major = getVersion('major');
	my $minor = getVersion('minor');
	my $build = getVersion('build');
	my $date = getVersion('date');
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



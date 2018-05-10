#!/usr/bin/perl

use warnings;
use strict;
use Cwd 'abs_path';
use File::Basename;
use Getopt::Long qw(:config pass_through);

my $script = __FILE__;
my $dir = dirname($script);
my $debug = "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n";#,address=12121
my $httpRoot = "$dir/../http-root";
my $log = "";
#my $debug = "";
#my $log = "-Dlog.to.stdout=true";
my $kill = undef;
my $daemon = undef;
my $xmx = "100m";
my $jvm_args = "";
my $workarea = undef;

GetOptions(
    "kill"   => \$kill,
    "daemon"   => \$daemon,
    "xmx"   => \$xmx,
    "jvm-args" => \$jvm_args,
    "workarea" => \$workarea
);

if ($kill)
{
	print "killing existing peer\n";
	my $grepCmd = "ps -efww | grep codeine.CodeinePeerBootstrap | grep -v grep";
	print "grep:\n";
	system($grepCmd);
	system("kill `$grepCmd | awk '{print \$2}' | tr '\n' ' '`");
	sleep 5;
}

if ($workarea) {
    $ENV{CODEINE_WORKAREA} = $workarea;
}

my $cmdLine = "$ENV{'JAVA_HOME'}/bin/java $debug $log -Xmx$xmx $jvm_args -cp $dir/codeine.jar codeine.CodeinePeerBootstrap";
print "starting peer\n";
if ($daemon)
{
	system("/usr/bin/nohup $cmdLine > $httpRoot/codeine.peer.out.log 2>&1 < /dev/null &");
}
else
{
	system("$cmdLine");
}

#!/usr/bin/perl
use strict;

my $kill_current = 0;
my @ARGV2        = ();
foreach my $arg (@ARGV)
{
  if ( $arg eq "--kill_current" || $arg eq "-kill_current" )
  {
    $kill_current = 1;
    next;
  }
  push( @ARGV2, $arg );
}

print "DEBUG: KILL is ON\n";
@ARGV = @ARGV2;
die
"Usage: startYamiClient <java> <rsync> <rsync user> <client port> <server port> <client install dir> <conf file name>  <rsync source>\n"
  if ( @ARGV < 8 );
die "Should be run by root\n" if ($>);

#
my $java           = shift;
my $rsync          = shift;
my $user           = shift;
my $client_port    = shift;
my $server_port    = shift;
my $client_install = shift;
my $conf_file      = shift;
my $rsync_source   = shift;
my $hostname       = `su - nobody -c 'hostname --fqdn'`;

#
my $start_log = "/tmp/yami_start";
my $PS        = "/bin/ps";
my $FLAGS     = "-avz --delete --delete-before --exclude .svn/";
my $rsync_cmd =
  "/bin/su - $user -c \"$rsync $FLAGS $rsync_source $client_install/\"";

# wait for current java to shutdown:
sleep 4;

&create_fs;

&rsync_dist;

# current peer instance should be shutdown by the already running java itself.
if ($kill_current)
{
  &kill_current_instance;
}

&start_peer_java;

sleep 4;

my @pids = &get_running_pids;
if ( not( scalar(@pids) ) )
{
  print
    "Failed to start yami client, check $start_log for more information\n";
}
else
{
  print "Yami Client started successfully as @pids\n";
}

###############################################################################
#               FUNCTIONS                   #
###############################################################################

sub create_fs
{
  if ( not -e $client_install )
  {
    print "DEBUG: creating dir $client_install\n";
    `mkdir -p $client_install`;
  }
  my $chown_cmd = "chown -R $user:root $client_install";
  print "DEBUG: running $chown_cmd\n";
  `$chown_cmd`;
}

sub rsync_dist
{
  print "DEBUG: will run $rsync_cmd\n";
  `$rsync_cmd`;
  warn "SCRIPT ERROR: Failed to run ($rsync_cmd)(exit code $?)\n" if ($?);
}

sub start_peer_java
{
  my $java_cmd =
"/usr/bin/nohup $java -Djava.path=$java -Ddebug=true -Drsync.path=$rsync -Dyami.conf=$conf_file -Drsync.user=$user -Dclient.port=$client_port -Drsync.source=$rsync_source -cp $client_install/bin/yami.jar yami.YamiClientBootstrap > $start_log 2>&1 < /dev/null &";
  print "DEBUG: will run ($java_cmd)\n";
  `$java_cmd`;
}

sub kill_current_instance
{
  my @pids = &get_running_pids();

  if ( scalar @pids > 0 )
  {
    if ( scalar @pids == 1 )
    {
      print "DEBUG: going to kill previous instance $pids[0]\n";
      kill( 9, @pids );
      sleep 5;
    }
    else
    {
      print
"ERROR: found more than 1 previous instance of this yami monitor\n";
    }
  }
  else
  {
    print "DEBUG: didn't find previous instances for this monitor\n";
  }
}

sub get_running_pids
{
  my $pid_cmd =
"$PS -eo pid,cmd | grep java | grep 'yami.YamiClientBootstrap'| awk '{print \$1}'";
  print "DEBUG: will run ($pid_cmd)\n";
  my @pids = `$pid_cmd`;
  chomp @pids;
  my @matching_pids = ();
  foreach my $pid (@pids)
  {
    if ( &is_pid_port_match($pid) )
    {
      push(@matching_pids,$pid);
    }
  }
  print "DEBUG: found PIDS: (" . join(" ",@matching_pids) . ")\n";
  return @matching_pids;
}

sub is_pid_port_match
{
  my $pid = shift;
  my @out = `lsof -p $pid | grep 'TCP \\*:$client_port (LISTEN)'`;
  chomp @out;
  return 0 if ( $? || scalar(@out) != 1 );
  return 1;
}
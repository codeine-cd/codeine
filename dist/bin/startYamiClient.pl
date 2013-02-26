#!/usr/bin/perl
use strict;
use Cwd 'abs_path';
use File::Basename;
use Getopt::Long qw(:config pass_through);
use Data::Dumper;
my @ARGV_CPY = @ARGV;

my $DIST_DIR;
my $LIB_DIR;
my $START_LOG   = "/tmp/yami_start";
my $RSYNC_FLAGS = "-avz --delete --delete-before --exclude .svn/";

BEGIN
{
	$DIST_DIR = abs_path( dirname( abs_path($0) ) . "/.." );
	$LIB_DIR  = $DIST_DIR . "/perl-lib";
}

die "Can't find perl-lib dir (should include XML::Simple)\n"
  if ( not -e $LIB_DIR );
use lib "$LIB_DIR";
use XML::Simple;

# set default values (should be overidden by conf file or flags):
my $debug  = 0;
my $update = 0;
my $kill   = 0;

my %opt            = ();
my %fopt           = ();
my $CONF_FILE      = "yami.conf.xml";
my $CLIENT_INSTALL = "/tmp/yami/";
my $HOSTNAME       = `su - nobody -c 'hostname --fqdn'`;

GetOptions(
	"debug"  => \$debug,
	"update" => \$update,
	"kill"   => \$kill
);
debug( "running as [" . abs_path($0) . "]" );

GetOptions( \%opt, "conf_file=s" );
if ( exists( $opt{'conf_file'} ) )
{
	$CONF_FILE = $opt{'conf_file'};
}
else
{
	Warn("conf_file not specified, using default: $CONF_FILE");
}

my $CONF_FILE_FULL = "$DIST_DIR/conf/$CONF_FILE";

error("No configuration file '$CONF_FILE_FULL'")
  if ( not -e $CONF_FILE_FULL );

&get_conf_from_xml;
&get_conf_from_opt;

error( "Bad parameters: " . join( ",", @ARGV ) ) if (@ARGV);

if ($update)
{
	&update_fs;
	my $rerun_cmd = &recreate_exec;
	debug("Will rerun start script [$rerun_cmd]");
	exec($rerun_cmd);
}

if ($kill)
{
	&kill_current_instance();
}

&start_peer_java;

my @pids = &get_running_pids;
if ( not( scalar(@pids) ) )
{
	error("Failed to start yami client, check $START_LOG for more information");
}
elsif ( scalar(@pids) == 1 )
{
	print "Yami Client started successfully as @pids\n";
}
else
{
	Warn( "Found several matching pids, not sure if started successfully: "
		  . join( ",", @pids ) );
}

exit 0;

###################				###################
#					Subroutines					  #
###################				###################
sub start_peer_java
{
	my $java_cmd = "/usr/bin/nohup " . get_opt('java') . " -Xmx200m ";
	foreach my $o ( keys %opt )
	{
		$java_cmd .= "-D$o=$opt{$o} ";
	}
	$java_cmd .=
"-cp $CLIENT_INSTALL/bin/yami.jar yami.YamiClientBootstrap > $START_LOG 2>&1 < /dev/null &";
	debug("Will run [$java_cmd]");
	`$java_cmd`;
}

sub get_conf_from_xml
{
	my $xml  = new XML::Simple;
	my $data = $xml->XMLin($CONF_FILE_FULL);
	debug("Loaded XML file $CONF_FILE");
	$fopt{'conf_file'}    = $data->{'conf'}->{'conf_file'};
	$fopt{'java'}         = $data->{'conf'}->{'java'};
	$fopt{'rsync'}        = $data->{'conf'}->{'rsync'};
	$fopt{'rsync_user'}   = $data->{'conf'}->{'rsync_user'};
	$fopt{'rsync_source'} = $data->{'conf'}->{'rsync_source'};
	$fopt{'client_port'}  = $data->{'conf'}->{'client_port'};
	$fopt{'server_port'}  = $data->{'conf'}->{'server_port'};
	$fopt{'client_path'}  = $data->{'conf'}->{'client_path'};

	$CONF_FILE      = $fopt{'conf_file'} if ( defined $fopt{'conf_file'} );
	$CONF_FILE_FULL = "$DIST_DIR/conf/$CONF_FILE";
	$CLIENT_INSTALL = $fopt{'client_path'}
	  if ( defined $fopt{'client_path'} );
	debug( "CI:" . $CLIENT_INSTALL );
}

sub get_conf_from_opt
{
	debug("Loading options");
	Getopt::Long::Configure( ("no_pass_through") );
	my $result =
	  GetOptions( \%opt, "java=s", "rsync=s", "rsync_user=s", "rsync_source=s",
		"client_port=i", "server_port=i", "client_install=s" );
	exit 2 if ( not $result );
	$CONF_FILE      = $opt{'conf_file'} if ( defined $opt{'conf_file'} );
	$CONF_FILE_FULL = "$DIST_DIR/conf/$CONF_FILE";
	$CLIENT_INSTALL = $opt{'client_path'}
	  if ( defined $opt{'client_path'} );
	debug( "CI:" . $CLIENT_INSTALL );
}

sub kill_current_instance
{
	my $port = shift;

	#debug("Will try to kill instance on port $port");
	print Dumper(%opt) . "TEST";
}

sub update_fs
{
	&create_fs;
	&rsync_dist;
}

sub recreate_exec
{
	my $cmd = get_opt('client_path') . "/bin/" . basename($0);
	foreach my $key ( keys %opt )
	{
		$cmd .= " " . "--$key" . " " . "$opt{$key}";
	}
	$cmd .= " --debug" if ($debug);
	$cmd .= " --kill"  if ($kill);
	return $cmd;
}

sub create_fs
{
	if ( not -e $CLIENT_INSTALL )
	{
		debug("Creating dir $CLIENT_INSTALL");
		`mkdir -p $CLIENT_INSTALL`;
	}
	my $chown_cmd =
	  "chown -R " . get_opt('rsync_user') . ":root " . get_opt('client_path');
	debug("Running '$chown_cmd'");
	`$chown_cmd`;
	error("Failed to run '$chown_cmd'") if ($?);
}

sub rsync_dist
{
	my $RSYNC_CMD =
	    "/bin/su - "
	  . get_opt('rsync_user')
	  . " -c \""
	  . get_opt('rsync')
	  . " $RSYNC_FLAGS "
	  . get_opt('rsync_source') . " "
	  . get_opt('client_path') . "/\"";
	debug("Will run [$RSYNC_CMD]");
	my @out = `$RSYNC_CMD`;
	if ($?)
	{
		Warn(@out);
		error("rsync failed '$RSYNC_CMD'");
	}
}

sub kill_current_instance
{
	my @pids = &get_running_pids();

	if ( scalar @pids > 0 )
	{
		if ( scalar @pids == 1 )
		{
			debug("Going to kill previous instance $pids[0]");
			kill( 9, @pids );
			debug("sleep(5)...");
			sleep 5;
		}
		else
		{
			Warn(   "Found more than one instance: "
				  . join( ",", @pids )
				  . ". Not killing anything." );
		}
	}
	else
	{
		debug("Didn't find previous instances for this monitor.");
	}
}

sub get_running_pids
{
	my $pid_cmd =
"/bin/ps -eo pid,cmd | grep java | grep 'yami.YamiClientBootstrap'| awk '{print \$1}'";
	debug("will run [$pid_cmd]");
	my @pids = `$pid_cmd`;
	chomp @pids;
	my @matching_pids = ();
	foreach my $pid (@pids)
	{
		if ( &is_pid_port_match($pid) )
		{
			push( @matching_pids, $pid );
		}
	}
	debug( "Found PIDS: (" . join( " ", @matching_pids ) . ")" );
	return @matching_pids;
}

sub is_pid_port_match
{
	my $pid = shift;
	my $LSOF_CMD =
	  "lsof -p $pid | grep 'TCP \\*:" . get_opt('client_port') . " (LISTEN)'";
	debug("Running [$LSOF_CMD]");
	my @out = `$LSOF_CMD`;
	chomp @out;
	return 0 if ( $? || scalar(@out) != 1 );
	return 1;
}

sub get_opt
{
	my $o = shift;
	return $opt{$o} if ( defined $opt{$o} );
	return $fopt{$o};
}

sub println
{
	my $arg = shift;
	print $arg . "\n";
}

sub debug
{
	my @msgs = @_;
	chomp @msgs;
	foreach my $msg (@msgs)
	{
		println( "DEBUG: " . $msg ) if ($debug);
	}
}

sub Warn
{
	my $msg = shift;
	println( "WARN: " . $msg );
}

sub error
{
	my $msg = shift;
	die "ERROR: " . $msg . "\n";
}

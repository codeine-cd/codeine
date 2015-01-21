# Codeine

Codeine is a continuous deployment tool that makes product deployment faster and safer through automation and monitoring.

[![Build Status](https://travis-ci.org/codeine-cd/codeine.png?branch=master)](https://travis-ci.org/codeine-cd/codeine)
[![Dependency Status](https://gemnasium.com/codeine-cd/codeine.svg)](https://gemnasium.com/codeine-cd/codeine)
[![Code Climate](https://codeclimate.com/github/codeine-cd/codeine/badges/gpa.svg)](https://codeclimate.com/github/codeine-cd/codeine)
[![Built with Grunt](https://cdn.gruntjs.com/builtwith.png)](http://gruntjs.com/)
[![Built with Maven](http://maven.apache.org/images/logos/maven-feather.png)](http://maven.apache.org)
[![Stories in Ready](https://badge.waffle.io/codeine-cd/codeine.svg?label=ready&title=Ready)](http://waffle.io/codeine-cd/codeine)


## Getting started

### Installing Codeine

1. Download the latest release from [here](https://github.com/codeine-cd/codeine/releases).
2. Make a directory called `codeine`:

        mkdir codeine

3. cd to this new directory:

        cd codeine

4. Make directories called `workarea` and `deployment`:

        mkdir workarea
        mkdir deployment

5. Make a directory inside `workarea` called `conf`:

        mkdir workarea/conf

6. cd to the `deployment` directory:

        cd deployment

7. Unzip the file that you downloaded here. 
8. cd to the `workarea/conf` directory:

        cd ../workarea/conf

9. If you want Codeine to run its own MySQL instance (only compiled for and tested on Suse Linux 10 and 11, 64-bit; may work on other versions, YMMV):
    1. Create a directory under `codeine` called `mysql_lib`:
            
            cd ../..
            mkdir mysql_lib

    2. Grab the contents of the `libs/mysql` directory from the Codeine repository. The easiest way to do this is with svn:

            svn export https://github.com/codeine-cd/codeine/trunk/libs/mysql

10. Create a file called `codeine.conf.json` that contains the following:

        {
        "web_server_host": "<hostname>",
        "web_server_port": <port>,
        "admin_mail": "<email_address>",
        "mysql":[{
                "host": "<db_hostname>",
                "port": <db_port>,
                "dir": "workarea/mysql_work",
                "bin_dir":"mysql_lib",
                "user": "<user>",
                "password": "<password>",
                "managed_by_codeine": true
                }]
        }

    Replace the placeholders with actual values.

    **Note:** If you are running your own MySQL instance (instead of letting Codeine run one for you):

    1. Omit the `"dir"` and `bin_dir` lines.
    2. Change the MySQL user and password from `codeine` to the ones for your MySQL server.
    3. Change `"managed_by_codeine"` to `false`.

### Starting Codeine

1. On both the server machine and on each client machine, set the value of the `JAVA_HOME` environment variable. The path varies depending on the Linux distribution, local environment, etc.:

        setenv JAVA_HOME /path/to/jdk

    (This is for tcsh. For bash, use `export` instead of `setenv`.)
2. On both the server machine and on each client machine, set the value of the `CODEINE_WORKAREA` environment variable:

        setenv CODEINE_WORKAREA /full/path/to/workarea

    (This is the workarea directory that you created above.)
3. Start the server. From the `codeine` directory, type:

        deployment/bin/run-server.pl

you can now see it at the following link:

        http://localhost:<port>/        

4. On each client machine, run the Codeine client. From the `codeine` directory, type:

        deployment/bin/run-peer.pl

You can see all the connected clients by going to **Manage Codeine | Codeine Nodes Info**:
        http://localhost:<port>/codeine-nodes?project=Codeine_Internal_Nodes_Project&version=All+versions


## Basic terms
* Node&mdash;an application that is deployed on a single machine (including Codeine itself)
* Project&mdash;a set of homogeneous nodes
* Monitor&mdash;a script that runs on each node of a project to monitor its status. A project can have multiple monitors.
* Command&mdash;a script that runs on selected nodes of a project, to execute some action (for example, deploying a new version). A project can have multiple commands.

## Features
* Deployment and upgrades of new versions
 * Script based automation
 * Immediate/progressive execution
 * Full control during deployment: progress view, manual cancel, etc.&hellip;
 * Fully customizable as needed through a simple interface
* Monitoring:
 * Script-based
 * Email notifications
 * Automatic trigger for deployment cancellation
* Permissions management
 * Access level separation per user/project (view/command/configure)
 * Easily manageable
* Web view with all the deployment information of your project in one place:
 * Aggregated view based on versions
 * Filtering of nodes by name or tag
 * Status of the nodes of your application
 * Command history for previous deployments
 * Graph view of nodes' status


codeine
====

how to install:
* clone the repository
* dist directory is the root for the application (cd dist)
* configuration is at conf/codeine.conf.xml (see example in conf/example-conf.xml)
* to start server: java -jar bin/codeine.jar


features:
* each monitor is a script in monitors dir. 
exit status 0 is success and other exit status is failure.
* monitors/version script will enable version for each node. 
the script output should be the version
* bin/resatrtAllPeers script will enable restart of all peers.
* to switch version: <switchVersionEnabled>true</switchVersionEnabled> in configuration.
in addition add bin/switch-version.

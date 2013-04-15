yami
====

how to install:
1. clone the repository
2. dist directory is the root for the application (cd dist)
3. configuration is at conf/yami.conf.xml (see example in conf/example-conf.xml)
4. to start server: java -jar bin/yami.jar

features:
* each monitor is a script in monitors dir. 
exit status 0 is success and other exit status is failure.
* monitors/version script will enable version for each node. 
the script output should be the version
* bin/resatrtAllPeers script will enable restart of all peers.
* to switch version: <switchVersionEnabled>true</switchVersionEnabled> in configuration.
in addition add bin/switch-version.
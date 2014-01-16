# Codeine

Codeine is a Continuous Deployment tool that enables teams to deploy their product faster and safer using automation.

[![Build Status](https://travis-ci.org/Intel-IT/codeine.png?branch=master)](https://travis-ci.org/Intel-IT/codeine)

## Getting started

### How to install:
* download latest zip file
* future: java -jar codeine.zip
* cd deployment
* unzip to that directory
* set environment variables: JAVA_HOME, CODEINE_WORKAREA
* execute: deployment/bin/run-server.pl

### Configure MySql
TBD

### Basic terms
* Node - an application that is deployed on a single machine
* Project - a set of homogeneous nodes
* Monitor - a script that runs on each node of a project to monitor its status. Project can have multiple monitors.
* Command - a script that runs on selected nodes of a project, to execute some action (deploying a new version for example). Project can have multiple commands.

## Features:
* Deployment and upgrades of new versions
 * Script based automation
 * Immediate / Progressive execution
 * Full control during deployment: progress view, manual cancel etc...
 * Fully customized when needed with simple interface
* Monitoring
 * Script based
 * Email notification
 * Automatic trigger for deployment cancellation
* Permissions management
 * Access level separation per user / project (view / command / configure)
 * Easily manageable
* Web view with all the deployment information of your project in one place
 * Aggregated view based on versions
 * Filtering of Nodes by Name or Tag
 * Status of the nodes of your application
 * Commands history for previous deployments
 * Graph view of nodes status


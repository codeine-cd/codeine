(function (angular) {
    'use strict';

    //// JavaScript Code ////


    //// Angular Code ////
    angular.module('codeine')
        .constant('HelpConstants', {
            "howToRunHelp" : "Select a command execution strategy.<br/><br/><strong>'Single'</strong> - allows command only on a single node at a time.<br/><br/>" +
                "<strong>'Immediately'</strong> - run command on nodes without delay.<br/><br/>" +
                "<strong>'Progressive'</strong> - run command on nodes gradually at configured rate.<br/><br/>",
            "monitorHelp" : "Select which nodes to show.<br/><br/><strong>'Any Alert'</strong> - show nodes with alerts (no matter which monitor).<br/><br/>" +
                "<strong>'All Nodes'</strong> - also show nodes without alerts.<br/><br/>" +
                "<strong>Specific Monitor</strong> - show only nodes with alerts from that monitor.",
            "commandNodesFilterHelp" : "Select the nodes on which the command will be executed on nodes of checked versiosns. <br/><br/><strong>'All Selected'</strong> - All the nodes in the selected versions.<br/><br/>" +
                "<strong>'Only Failing'</strong> - Only on nodes that have failing monitors.<br/><br/>" +
                "<strong>'Limited Number'</strong> - the command will be executed on this number of nodes from all selected versions (total).<br/><br/>",
            "ratioHelp" : "The rate for command execution.<br/><br/><strong>'Linear'</strong> - Spread nodes evenly in the specified time period.<br/><br/>" +
                "<strong>'Exponential'</strong> - execute on one node first, then increase number of nodes exponentially (1, 2, 4, ...).<br/><br/>",
            "discardOldCommandsEnabledHelp" : "Controls the disk consumption of Codeine by managing how long you'd like to keep records of the commands (such as console output)",
            "discardMaxCommandsHelp" : "You can have Codeine make sure that it only maintains up to N command records. If a new command is started, the oldest record will be simply removed.",
            "discardMaxDaysHelp" : "You can have Codeine delete a record if it reaches a certain age (for example, 7 days old).",
            "projectEnvVarsHelp" : "Set environment variables that will be available for monitors and commands execution environment.",
            "command_patameter_description_Help" : "A description that will be displayed as a tooltip to the user executing the command.",
            "command_patameter_values_Help" : "Comma-delimited list of parameters for user selection. For example: <strong>1,2,3</strong>.",
            "command_patameter_name_Help" : "The name of the parameter, will also be used as environment variable in the executing command.",
            "command_patameter_default_val_Help" : "The default value that will be suggested to the user when executing the command.",
            "command_patameter_validation_expression_Help" : "A regular expression that will validate the parameter in the UI.<br/><br/>" +
                "For example: <strong>\\S*</strong> - a word, <strong>\\d</strong> - one digit number, <strong>.*</strong> - not empty (like required).",
            "concurrencyHelp" : "On how many nodes to execute the command in parallel.",
            "stopOnErrorHelp" : "Check for errors during command execution, and stop commanding more nodes if the percent of nodes with failing monitors is greater than the configured value.",
            "durationHelp" : "The time period for the command execution. Codeine will spread execution on nodes across that time based on the ratio.",
            "preventOverrideHelp" : "If checked, when executing the command, the user will not be able to change the configuration below.",
            "commandsConfigureHelp" : "Configure commands to execute on node. Such command can upgrade to new version or restart the process.",
            "commandHistoryHelp" : "All the currently running and finished commands of the selected project",
            "commandExecutorHelp" : "All the currently running commands on the Codeine server from all projects",
            "nodeTagsHelp" : "Tags on nodes.<br/>Press a tag to filter only nodes with that tag (blue).",
            "emailHelp" : "Codeine will notify on failing monitors to configured users. " +
                "<br/><br/><strong>'Immediately'</strong> - send mail immediately after a monitor has failed." +
                "<br/><br/><strong>'Hourly'/'Daily'</strong> - send mail after a monitor has failed, Codeine will aggregate the notifications to send them at the specified frequency.",
            "nodesHelp" : "Configure how Codeine detects nodes for the project." +
                "<br/><br/><strong>'Configuration'</strong> - nodes are statically configured." +
                "<br/><br/><strong>'Reporter'</strong> - nodes are dynamically created when they report to Codeine via the Codeine API." +
                "<br/><br/><strong>'Script'</strong> - nodes are dynamically discovered by a script.",
            "discoveryScriptHelp" : "Type a shell script to discover nodes for the project. The script runs on each individual Codeine peer.<br/><br/>" +
                "The script output should be written to a file in a path specified in the environment variable <strong>CODEINE_OUTPUT_FILE</strong> in the following json format: <pre>[{name:\"&lt;name&gt;\", alias:\"&lt;alias&gt;\"}, ...]</pre> <br/>" +
                "The script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME</strong>",
            "tagsdiscoveryScriptHelp" : "Type a shell script to discover tags for the project's nodes. The script runs on each individual Codeine peer.<br/><br/>" +
                "The script output should be written to a file in a path specified in the environment variable <strong>CODEINE_OUTPUT_FILE</strong> in the following json format: <pre>[\"&lt;tag-string&gt;\", ...]</pre> <br/>" +
                "The script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME, CODEINE_NODE_NAME, CODEINE_NODE_ALIAS</strong>",
            "commandScriptHelp" : "Type a shell script to execute on nodes.<br/><br/>" +
                "Parameters will be provided to the script as <strong>Environment Variables</strong>.<br/>" +
                "In addition, the script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME, CODEINE_NODE_NAME, CODEINE_NODE_ALIAS, CODEINE_NODE_TAGS</strong>",
            "monitorsConfigureHelp" : "Monitors used to detect malfunctions and errors on the running nodes.<br/><br/>" +
                "Codeine runs the monitors periodically on all nodes, and notifies when a node fails.",
            "collectorsConfigureHelp" : "Collectors used to detect malfunctions and errors on the running nodes, and also to provide information about nodes status.<br/><br/>" +
                "Codeine runs the collectors periodically on all nodes, and notifies when a node fails.",
            "versionHelp" : "A version will be reported by each node.",
            "minIntervalHelp" : "The minimal interval that Codeine will run the collector.<br/><br/>" +
                "This option is good when the collector execution itself is time consuming or may cause load on the node itself and hence should not executed at high frequency.<br/><br/>" +
                "Specify an <strong>integer in minutes</strong>. Default is less than a minute.",
            "commandTimeoutHelp" : "The maximum time in <strong>minutes</strong> command will be allowed to run on node, before termination",
            "credentialsHelp" : "The user that will be used to execute the configured script.<br/><br/> This feature does not work for most cases right now.",
            "notificationsEnabledHelp" : "If checked, emails will be sent to the configured users, when this collector starts to fail.",
            "monitorScriptHelp" : "Type a shell script to monitor the desired functionality of the node.<br/><br/>By convention, monitors are marked as failed if their <strong>exit status is non-zero</strong>.<br/><br/>" +
                "The script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME, CODEINE_NODE_NAME, CODEINE_NODE_ALIAS, CODEINE_NODE_TAGS</strong>.",
            "collectorScriptHelp" : "Type a shell script to collector the desired functionality of the node.<br/><br/>By convention, collectors are marked as failed if their <strong>exit status is non-zero</strong>.<br/><br/>" +
                "The script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME, CODEINE_NODE_NAME, CODEINE_NODE_ALIAS, CODEINE_NODE_TAGS</strong>.<br/>"+
                "The collector output is expected in a file, that its name is in <strong>CODEINE_OUTPUT_FILE</strong> environment variable.",
            "versionScriptHelp" : "Type a shell script to specify the node's version.<br/><br/>" +
                "The script output should be written to a file in a path specified in the environment variable <strong>CODEINE_OUTPUT_FILE</strong> and should contain the version number (e.g.: 1.0.3).<br/><br/>" +
                "The script can use the following environment variables in execution: <strong>CODEINE_PROJECT_NAME, CODEINE_NODE_NAME, CODEINE_NODE_ALIAS, CODEINE_NODE_TAGS</strong>.",
            "tabsConfigureHelp" : "Enables a separate view for a group of projects in the main page of Codeine.",
            "serverNameHelp" : "A name to be displayed when accessing UI or in mail messages",
            "webServerHostHelp" : "Full host name of the web server",
            "webServerPortHelp" : "Port of the web server (requires restart)",
            "directory_hostHelp" : "Full host name of the directory server",
            "directory_portHelp" : "Port of the directory server",
            "mysql_hostHelp" : "Full host name of the MySQL server",
            "MysqlPortConfigureHelp" : "Port of the MySQL server",
            "MysqlDirConfigureHelp" : "A directory for MySQL data",
            "MysqlBinDirConfigureHelp" : "The MySQL binaries directory (mysqld, ...)",
            "AdminMailConfigureHelp" : "The Codeine admin's email",
            "AuthenticationMethodConfigureHelp" : "The way Codeine will authenticate users.<br/><strong>Disabled</strong> - No authentication<br/><strong>Builtin</strong> - Codeine internal database will store users<br/><strong>WindowsCredentials</strong> - Using spnego to authenticate users<br/>",
            "rolesConfigureHelp" : "Internal domains for Windows credentials",
            "permissionsConfigureHelp" : "Configure access privileges for the users of Codeine.",
            "tabExpressionHelp" : "Comma-separated list of project names or regular expressions",
            "adminPermissionsConfigureHelp" : "Is administrator of Codeine",
            "viewProjectPermissionsConfigureHelp" : "List of projects (or regular expression) that the user can view only",
            "commandProjectPermissionsConfigureHelp" : "List of projects (or regular expression) for which the user can view and execute commands",
            "configProjectPermissionsConfigureHelp" : "List of projects (or regular expression) for which the user has full privileges",
            "ProjectPermissionsConfigureSection" : "Configure the users/groups and their permissions on this project",
            "ProjectPermissionsConfigureSection_read" : "Allow to view project nodes",
            "ProjectPermissionsConfigureSection_command" : "List of nodes (or regular expression) for which the user can execute commands",
            "ProjectPermissionsConfigureSection_configure" : "Allow to configure project (this page)",
            "MysqlUserConfigureHelp" : "Username for MySQL connections",
            "MysqlPasswordConfigureHelp" : "Password for MySQL connections",
            "MysqlManagedByCodeineConfigureHelp" : "Codeine is responsible to start/stop MySQL server instance.",
            "codeineConfigureHelp" : "Here the admin can configure all the Codeine global settings.<br/>Some changes might require a restart.",
            "codeineAdminHelp" : "Here the admin can perform operations on codeine.",
            "projectDescriptionHelp" : "Description is a convenient way to let users know what this project is about.",
            "commandDescriptionHelp" : "Description is a convenient way to let users know what this command is doing and for what purpose it should be used.",
            "projectIncludeCommandsFromHelp" : "List here projects to include their configured commands in this project",
            "typeHelp" : "Select a type for the parameter for command execution",
            "collectorDescriptionHelp" : "Description will be displayed to explain what the collector is doing",
            "nodesPerMinuteHelp" : "An estimation of the execution frequency",
            "collectorTypeHelp" : "Select a type for collector. types are:<br/>"+
                "<br/><strong>String</strong> - text value that will be displayed to user"+
                "<br/><strong>Number</strong> - numerical value that will be displayed to user and can be a base for more calculations"+
                "<br/><strong>Monitor</strong> - indicates failures or success by the script exit status (zero is success).",
            "operatingSystemHelp" : "Type of operating system for this project, will define the way scripts run in this project (shell/cmd)"
        });

})(angular);
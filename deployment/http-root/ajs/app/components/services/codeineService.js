(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineServiceFactory(Constants, $http, $rootScope) {

        var getApiPrefix = function(){
            if ($rootScope.disableToken) {
                return "/api";
            } else {
                return Constants.CODEINE_API_PREFIX;
            }
        };

        var Api = {
            getApiPrefix : getApiPrefix,
            getGlobalConfiguration: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/global-configuration", { cache: false });
            },
            getExperimentalConfiguration: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/experimental-configuration", { cache: false });
            },
            getSessionInfo: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/session-info", { cache: false });
            },
            getProjects: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/projects");
            },
            getProjectMonitorStatistics: function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/monitors-statistics", { params: { project: project }, cache: false });
            },
            getManageStatistics: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/manage-statistics", { cache: false });
            },
            getViewTabs: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/projects-tabs", { cache: false });
            },
            updateViewTabs: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/projects-tabs",data);
            },
            createProject: function(project) {
                return $http.post(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/projects", project,  { cache: false });
            },
            getProjectConfiguration: function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-configuration", { params: { project: project } , cache: false });
            },
            getProjectCommands: function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-commands-configuration", { params: { project: project } , cache: false });
            },
            deleteProject : function(project) {
                return $http.delete(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-configuration", { params: { project: project } });
            },
            saveProjectConfiguration : function(configuration) {
                return $http.put(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-configuration", configuration, {  params: { project: configuration.name } });
            },
            reloadProjectConfiguration : function(projectName) {
                return $http.post(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-configuration?project=" + encodeURIComponent(projectName));
            },
            pushProjectsToDb : function() {
                return $http.post(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/push-projects-to-db");
            },
            prepareForShutdown: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/prepare-for-shutdown", { cache: false });
            },
            cancelShutdown: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/cancel-shutdown", { cache: false });
            },
            getPermissions: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/permissions", { cache: false });
            },
            updatePermissions: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/permissions", data);
            },
            updateGlobalConfiguration: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + getApiPrefix() + '/global-configuration',data);
            },
            login: function(user,password) {
                return $http.post(Constants.CODEINE_WEB_SERVER + '/j_security_check',"j_username=" + encodeURIComponent(user) +"&j_password=" + encodeURIComponent(password), { headers: {'Content-Type': 'application/x-www-form-urlencoded'} });
            },
            logout: function() {
                return $http.get('/logout');
            },
            register: function(user, password) {
                return $http.post('/register',{username : user, password:  password });
            },
            getNodeStatus : function(project, node) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/node-status", {params: { project: project, node: node }, cache: false });
            },
            getMonitorStatus : function(project, node, monitor) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/monitor-status", {params: { project: project, node: node, monitor: monitor }, cache: false });
            },
            getCollectorStatus : function(project, node, collector) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/collector-status", {params: { project: project, node: node, collector: collector }, cache: false });
            },
            getCommandStatus : function(project, command_id) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/command-status", {params: { project: project, command: command_id } ,cache: false });
            },
            getProjectStatus : function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-status2", {params: { project: project } ,cache: false });
            },
            getProjectCommandHistory: function(project,node) {
                var paramsForRequest = { project: project };
                if (node !== undefined) {
                    paramsForRequest.node = node;
                }
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/commands-log", {params:  paramsForRequest ,cache: false });
            },
            getRunningCommands: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/commands-status", { cache: false });
            },
            runCommand : function(command,nodes) {
                return $http.post(Constants.CODEINE_WEB_SERVER + getApiPrefix() + '/command-nodes', { node_name_list : _.map(nodes, function(node){return node.name;}), command_info : command },{params: { project: command.project_name, redirect :false }});
            },
            getUserInfo : function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/user-info", { cache: false });
            },
            getNodesCsv : function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/csv-nodes", {params: { project: project } ,cache: false });
            },
            getProjectNodesAliases : function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/project-nodes-aliases", {params: { project: project } ,cache: false });
            },
            cancelCommand : function(project,commandId) {
                return $http.delete(Constants.CODEINE_WEB_SERVER + getApiPrefix() + "/command-nodes", { params: { 'project': project, 'command-id': commandId } });
            }

        };
        return Api;
    }


    //// Angular Code ////
    angular.module('codeine').factory('CodeineService', CodeineServiceFactory);

})(angular);

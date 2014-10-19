(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineServiceFactory(Constants, $http ) {

        var Api = {
            getGlobalConfiguration: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/global-configuration", { cache: false });
            },
            getExperimentalConfiguration: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/experimental-configuration", { cache: false });
            },
            getSessionInfo: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/session-info", { cache: false });
            },
            getProjects: function () {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/projects", { cache: false });
            },
            getProjectMonitorStatistics: function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/monitors-statistics", { params: { project: project }, cache: false });
            },
            getManageStatistics: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/manage-statistics", { cache: false });
            },
            getViewTabs: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/projects-tabs", { cache: false });
            },
            updateViewTabs: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + "/api/projects-tabs",data);
            },
            createProject: function(project) {
                return $http.post(Constants.CODEINE_WEB_SERVER + "/api/projects", project,  { cache: false });
            },
            getProjectConfiguration: function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/project-configuration", { params: { project: project } , cache: false });
            },
            deleteProject : function(project) {
                return $http.delete(Constants.CODEINE_WEB_SERVER + "/api/project-configuration", { params: { project: project } });
            },
            saveProjectConfiguration : function(configuration) {
                return $http.put(Constants.CODEINE_WEB_SERVER + "/api/project-configuration", configuration, {  params: { project: configuration.name } });
            },
            prepareForShutdown: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/prepare-for-shutdown", { cache: false });
            },
            cancelShutdown: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/cancel-shutdown", { cache: false });
            },
            getPermissions: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/permissions", { cache: false });
            },
            updatePermissions: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + "/api/permissions", data);
            },
            updateGlobalConfiguration: function(data) {
                return $http.put(Constants.CODEINE_WEB_SERVER + '/api/global-configuration',data);
            },
            login: function(user,password) {
                return $http.post(Constants.CODEINE_WEB_SERVER + '/j_security_check',"j_username=" + user +"&j_password=" + password, { headers: {'Content-Type': 'application/x-www-form-urlencoded'} });
            },
            logout: function() {
                return $http.get('/logout');
            },
            register: function(user, password) {
                return $http.post('/register',{username : user, password:  password });
            },
            getNodeStatus : function(project, node) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/node-status", {params: { project: project, node: node }, cache: false });
            },
            getMonitorStatus : function(project, node, monitor) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/monitor-status", {params: { project: project, node: node, monitor: monitor }, cache: false });
            },
            getCommandStatus : function(project, command_id) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/command-status", {params: { project: project, command: command_id } ,cache: false });
            },
            getProjectStatus : function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/project-status2", {params: { project: project } ,cache: false });
            },
            getProjectCommandHistory: function(project,node) {
                var paramsForRequest = { project: project };
                if (node !== undefined) {
                    paramsForRequest.node = node;
                }
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/commands-log", {params:  paramsForRequest ,cache: false });
            },
            getRunningCommands: function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/commands-status", { cache: false });
            },
            runCommand : function(command,nodes) {
                return $http.post(Constants.CODEINE_WEB_SERVER + '/api/command-nodes', { nodes : nodes, command_info : command },{params: { project: command.project_name, redirect :false }});
            },
            getUserInfo : function() {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/user-info", { cache: false });
            },
            getProjectNodes : function(project) {
                return $http.get(Constants.CODEINE_WEB_SERVER + "/api/project-nodes", {params: { project: project } ,cache: false });
            },
            cancelCommand : function(project,commandId) {
                return $http.delete(Constants.CODEINE_WEB_SERVER + "/api/command-nodes", { params: { 'project': project, 'command-id': commandId } });
            }

        };
        return Api;
    }


    //// Angular Code ////
    angular.module('codeine').factory('CodeineService', CodeineServiceFactory);

})(angular);

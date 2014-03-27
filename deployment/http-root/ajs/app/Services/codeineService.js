'use strict';

angular.module('codeine').factory('CodeineService', ['$rootScope', '$http', function ($rootScope, $http ) {

    var Api = {
        getGlobalConfiguration: function () {
            return $http.get("/global-configuration_json", { cache: false });
        },
        getExperimentalConfiguration: function () {
            return $http.get("/experimental-configuration_json", { cache: false });
        },
        getSessionInfo: function () {
            return $http.get("/session-info_json", { cache: false });
        },
        getProjects: function () {
            return $http.get("/projects_json", { cache: false });
        },
        getViewTabs: function() {
            return $http.get("/projects-tabs_json", { cache: false });
        },
        updateViewTabs: function(data) {
            return $http.put("/projects-tabs_json",data);
        },
        createProject: function(project) {
            return $http.post("/projects_json", project,  { cache: false });
        },
        getProjectConfiguration: function(project) {
            return $http.get("/project-configuration_json", { params: { project: project } , cache: false });
        },
        deleteProject : function(project) {
            return $http.delete("/project-configuration_json", { params: { project: project } });
        },
        saveProjectConfiguration : function(configuration) {
            return $http.put("/project-configuration_json", configuration, {  params: { project: configuration.name } });
        },
        prepareForShutdown: function() {
        	return $http.get("/prepare-for-shutdown", { cache: false });
        },
        cancelShutdown: function() {
        	return $http.get("/cancel-shutdown", { cache: false });
        },
        getPermissions: function() {
            return $http.get("/permissions_json", { cache: false });
        },
        updatePermissions: function(data) {
            return $http.put("/permissions_json", data);
        },
        updateGlobalConfiguration: function(data) {
            return $http.put('/global-configuration_json',data);
        },
        login: function(user,password) {
            return $http.post('/j_security_check',"j_username=" + user +"&j_password=" + password, { headers: {'Content-Type': 'application/x-www-form-urlencoded'} });
        },
        logout: function() {
            return $http.get('/logout');
        },
        register: function(user, password) {
            return $http.post('/register',{username : user, password:  password });
        },
        getNodeStatus : function(project, node) {
            return $http.get("/node-status_json", {params: { project: project, node: node }, cache: false });
        },
        getMonitorStatus : function(project, node, monitor) {
            return $http.get("/monitor-status_json", {params: { project: project, node: node, monitor: monitor }, cache: false });
        },
        getCommandStatus : function(project, command_id) {
            return $http.get("/command-status_json", {params: { project: project, command: command_id } ,cache: false });
        },
        getProjectStatus : function(project) {
            return $http.get("/project-status2_json", {params: { project: project } ,cache: false });
        },
        getProjectCommandHistory: function(project) {
            return $http.get("/commands-log_json", {params: { project: project } ,cache: false });
        },
        getRunningCommands: function() {
            return $http.get("/commands-status_json", { cache: false });
        },
        runCommand : function(command,nodes) {
            return $http.post('/command-nodes_json', { nodes : nodes, command_info : command },{params: { project: command.project_name, redirect :false }});
        },
        getUserInfo : function() {
            return $http.get("/user-info_json", { cache: false });
        },
        getProjectNodes : function(project) {
            return $http.get("/project-nodes2_json", {params: { project: project } ,cache: false });
        }

    };
    return Api;
}]);

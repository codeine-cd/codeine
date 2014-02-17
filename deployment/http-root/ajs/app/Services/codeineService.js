'use strict';

angular.module('codeine').factory('CodeineService', ['$rootScope', '$http', function ($rootScope, $http ) {

    var Api = {
        getGlobalConfiguration: function () {
            return $http.get("/global-configuration_json", { cache: false });
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
            return $http.get("/project-configuration_json", project,  { cache: false });
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
        }
    };
    return Api;
}]);

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
        getPermissions: function() {
            return $http.get("/permissions_json", { cache: false });
        },
        updatePermissions: function(data) {
            return $http.put("/permissions_json", data);
        },
        updateGlobalConfiguration: function(data) {
            return $http.put('/global-configuration_json',data);
        }
    };
    return Api;
}]);

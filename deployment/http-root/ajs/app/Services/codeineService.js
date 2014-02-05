'use strict';

angular.module('codeine').factory('CodeineService', ['$rootScope', '$http','$log', function ($rootScope, $http, $log) {

    var Api = {
        getGlobalConfiguration: function () {
            return $http.get("/global-configuration_json", { cache: false });
        },
        getSessionInfo: function () {
            return $http.get("/session-info_json", { cache: true });
        }
    };
    return Api;
}]);

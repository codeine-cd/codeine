'use strict';
angular.module('codeine').factory('viewAsInterceptor', ['$rootScope',function ($rootScope) {
    return {
        request: function (config) {
            if (angular.isDefined($rootScope.app.viewAs) && $rootScope.app.viewAs !== null && $rootScope.app.viewAs !== '' ) {
                config.headers = {'viewas':$rootScope.app.viewAs};
            }
            return config;
        }
    };
}]);
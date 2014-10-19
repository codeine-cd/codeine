(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function viewAsInterceptorFactory ($rootScope) {
        return {
            request: function (config) {
                if (angular.isDefined($rootScope.app.viewAs) && $rootScope.app.viewAs !== null && $rootScope.app.viewAs !== '' ) {
                    config.headers = {'viewas':$rootScope.app.viewAs};
                }
                return config;
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('viewAsInterceptor',viewAsInterceptorFactory);

})(angular);
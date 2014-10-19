(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function viewAsInterceptorFactory (LoginService) {
        return {
            request: function (config) {
                var viewAs = LoginService.getViewAs();
                if (viewAs && viewAs !== '' ) {
                    config.headers = {'viewas':viewAs};
                }
                return config;
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('viewAsInterceptor',viewAsInterceptorFactory);

})(angular);
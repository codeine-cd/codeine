'use strict';
angular.module('codeine')
.factory('ErrorHandler', ['$rootScope', '$log' , 'AlertService', function ($rootScope, $log, AlertService) {
    var ErrorHandler = this;

    ErrorHandler.handle = function (data, status, headers, config) {

        if ((config['url'] === '/j_security_check') || (config['url'] === '/register')) {
            $log.info('ErrorHandler: Ignoring error');
            return;
        }

        $log.error(angular.toJson(data));
        var message = [];
        if (data.title) {
            message.push("<strong>" + data.title + "</strong>");
        }
        if (data.message) {
            message.push(data.message);
        }
        AlertService.addAlert('danger', message.join('<br/>'));
    };

    return ErrorHandler;
}])
.factory('myHttpInterceptor', ['ErrorHandler', '$q', function (ErrorHandler, $q) {
    return {
        response: function (response) {
            return response;
        },
        responseError: function (response) {
            ErrorHandler.handle(response.data, response.status, response.headers, response.config);

            // do something on error
            return $q.reject(response);
        }
    };
}]);
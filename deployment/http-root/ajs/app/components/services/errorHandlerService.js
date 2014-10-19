(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function ErrorHandlerFactory($log, AlertService) {
        var ErrorHandler = this;

        ErrorHandler.handle = function (data, status, headers, config) {

            if ((config.url === '/j_security_check') || (config.url === '/register')) {
                $log.info('ErrorHandler: Ignoring error');
                return;
            }
            if(!data) {
                data = {
                    title : 'General Error',
                    message : 'There was an error while connecting to the server, it might be down or unreachable'
                };
            }
            $log.error('ErrorHandler: ' + angular.toJson(data));
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
    }

    function myHttpInterceptorFactory(ErrorHandler, $q) {
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

    }

    //// Angular Code ////
    angular.module('codeine').factory('ErrorHandler',ErrorHandlerFactory).factory('myHttpInterceptor',myHttpInterceptorFactory);

})(angular);
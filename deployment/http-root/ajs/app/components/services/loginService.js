(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function LoginServiceFactory($log, $q, $http, $interval, $location, CodeineService, ApplicationFocusService) {
        var viewAs,sessionInfo, deffer, initialized = false;

        function gettingSessionInfo(forceRefresh) {
            if (sessionInfo && !forceRefresh) {
                return $q.when(sessionInfo);
            }
            if (deffer) {
                return deffer.promise;
            }
            else {
                deffer = $q.defer();
                CodeineService.getSessionInfo().success(function (data) {
                    $log.debug('LoginService: got session info ' + angular.toJson(data));
                    sessionInfo = data;
                    deffer.resolve(data);
                    deffer = undefined;
                    initialized = true;
                }).error(function(error) {
                    deffer.reject(error);
                    deffer = undefined;
                });
                return deffer.promise;
            }
        }

        function getSessionInfo() {
            return sessionInfo;
        }

        function setViewAs(user) {
            viewAs = user;
            deffer = undefined;
            $http.defaults.headers.common.viewas = viewAs;
            return gettingSessionInfo(true).then(function() {

                $location.path('/codeine');
            },function(){
                $log.error('LoginService: Failed to get session info after view as');
                viewAs = undefined;
                delete $http.defaults.headers.common.viewas;
            });
        }

        function getViewAs() {
            return viewAs;
        }

        function init() {
            var promise = gettingSessionInfo();
            $interval(function() {
                if (!ApplicationFocusService.isInFocus() && (sessionInfo)) {
                    $log.debug('LoginService: Will skip sessionInfo refresh as app not in focus');
                }
                else {
                    deffer = undefined;
                    gettingSessionInfo(true);
                }
            },300000,0,false);
            return promise;
        }

        function isInitialized() {
            return initialized;
        }

        return {
            gettingSessionInfo : gettingSessionInfo,
            getSessionInfo : getSessionInfo,
            setViewAs : setViewAs,
            getViewAs : getViewAs,
            init : init,
            isInitialized : isInitialized
        };
    }


    //// Angular Code ////
    angular.module('codeine').factory('LoginService', LoginServiceFactory);

})(angular);
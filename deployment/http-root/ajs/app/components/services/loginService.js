(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function LoginServiceFactory($log, $q, $http, $interval, CodeineService, ApplicationFocusService) {
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
            var deferred = $q.defer();
            viewAs = user;
            deffer = undefined;
            $http.defaults.headers.common.viewas = viewAs;
            gettingSessionInfo(true).then(function() {
                deferred.resolve();

            },function(err){
                $log.error('LoginService: Failed to get session info after view as',err);
                viewAs = undefined;
                delete $http.defaults.headers.common.viewas;
                deferred.reject(err);
            });
            return deferred.promise;
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

        function prepareForShutdown() {
            return CodeineService.prepareForShutdown().success(function() {
                sessionInfo.isPrepareForShutdown = true;
            });
        }

        function cancelShutdown() {
            return CodeineService.cancelShutdown().success(function() {
                sessionInfo.isPrepareForShutdown = false;
            });
        }

        function login(user,password) {
            return CodeineService.login(user,password);
        }

        function logout() {
            return CodeineService.logout();
        }

        function register(user,password) {
            return CodeineService.register(user,password);
        }

        return {
            gettingSessionInfo : gettingSessionInfo,
            getSessionInfo : getSessionInfo,
            setViewAs : setViewAs,
            getViewAs : getViewAs,
            init : init,
            isInitialized : isInitialized,
            prepareForShutdown : prepareForShutdown,
            cancelShutdown : cancelShutdown,
            login : login,
            logout : logout,
            register : register
        };
    }


    //// Angular Code ////
    angular.module('codeine').factory('LoginService', LoginServiceFactory);

})(angular);
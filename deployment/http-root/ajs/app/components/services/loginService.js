(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function LoginServiceFactory($log, $q, $interval, $location, CodeineService, ApplicationFocusService) {

        var viewAs,sessionInfo, deffer;

        function gettingSessionInfo() {
            if (deffer) {
                return deffer;
            }
            else {
                deffer = $q.defer();
                CodeineService.getSessionInfo().success(function (data) {
                    $log.debug('LoginService: got session info ' + angular.toJson(data));
                    sessionInfo = data;
                    deffer.resolve(data);
                    deffer = undefined;
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
            return gettingSessionInfo().then(function() {
                $location.path('/codeine');
            },function(){
                $log.error('LoginService: Failed to get session info after view as');
                viewAs = undefined;
            });
        }

        function getViewAs() {
            return viewAs;
        }

        function init() {
            gettingSessionInfo();
            $interval(function() {
                if (!ApplicationFocusService.isInFocus() && (sessionInfo)) {
                    $log.debug('LoginService: Will skip sessionInfo refresh as app not in focus');
                }
                else {
                    deffer = undefined;
                    gettingSessionInfo();
                }
            },300000,0,false);
        }

        return {
            gettingSessionInfo : gettingSessionInfo,
            getSessionInfo : getSessionInfo,
            setViewAs : setViewAs,
            getViewAs : getViewAs,
            init : init
        };
    }


    //// Angular Code ////
    angular.module('codeine').factory('LoginService', LoginServiceFactory);

})(angular);
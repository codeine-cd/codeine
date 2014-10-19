'use strict';
angular.module('codeine').directive('userInfo', function ($rootScope, $log , $window, CodeineService, LoginService) {
    return {
        restrict: "E",
        transclude: false,
        scope: true,
        template : '<div ng-include="getTemplateUrl()"></div>',
        link: function ($scope) {
            $scope.logout = function() {
                $log.debug('userInfo: logout');
                CodeineService.logout().success(function() {
                    $window.location.reload();
                });
            };

            $scope.getTemplateUrl = function() {
                if (!LoginService.getSessionInfo() || !$scope.app.globalConfiguration) {
                    return "";
                }
                switch ($scope.app.globalConfiguration.authentication_method)
                {
                    case "Disabled":
                        return "/components/directives/user_info/empty.html";
                    case "Builtin":
                        if ((LoginService.getSessionInfo().permissions.username) && (LoginService.getSessionInfo().permissions.username !== 'Guest')) {
                            return "/components/directives/user_info/builtin-logged.html";
                        }
                        return "/components/directives/user_info/builtin-not-logged.html";
                    case "WindowsCredentials":
                        return "/components/directives/user_info/windowsCredentials.html";
                }
            };
        }
    };
});

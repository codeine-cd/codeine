angular.module('codeine').directive('userInfo', ['$rootScope','$log', '$window','CodeineService' , function ($rootScope, $log , $window, CodeineService) {
    return {
        restrict: "E",
        transclude: false,
        scope: true,
        template : '<div ng-include="getTemplateUrl()"></div>',
        link: function ($scope, element, attrs) {
            $log.debug("userInfo is linking...");

            $scope.logout = function() {
                $log.debug('userInfo: logout');
                CodeineService.logout().success(function() {
                    $window.location.reload();
                });
            }

            $scope.getTemplateUrl = function() {
                if (!$scope.app.sessionInfo || !$scope.app.globalConfiguration) return "";
                //$log.debug("userInfo: getTemplateUrl called, " + angular.toJson($scope.app.sessionInfo));
                switch ($scope.app.globalConfiguration["authentication_method"])
                {
                    case "Disabled":
                        return "/ajs/partials/directives/empty.html";
                    case "Builtin":
                        if (($scope.app.sessionInfo.permissions.username) && ($scope.app.sessionInfo.permissions.username !== 'Guest')) {
                            return "/ajs/partials/directives/builtin-logged.html";
                        }
                        return "/ajs/partials/directives/builtin-not-logged.html";
                    case "WindowsCredentials":
                        return "/ajs/partials/directives/windowsCredentials.html";
                }
            }
        }
    };
}]);

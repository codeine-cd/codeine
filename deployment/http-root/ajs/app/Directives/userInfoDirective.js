angular.module('codeine').directive('userInfo', ['$rootScope','$log',function ($rootScope, $log ) {
    return {
        restrict: "E",
        transclude: false,
        scope: true,
        template : '<div ng-include="getTemplateUrl()"></div>',
        link: function ($scope, element, attrs) {
            $log.debug("userInfo is linking...");
            $scope.getTemplateUrl = function() {
                if (!$scope.app.sessionInfo || !$scope.app.globalConfiguration) return "";
                $log.debug("userInfo: getTemplateUrl called, " + angular.toJson($scope.app.sessionInfo));
                switch ($scope.app.globalConfiguration["authentication_method"])
                {
                    case "Disabled":
                        return "/ajs/partials/directives/empty.html";
                    case "Builtin":
                        if ($scope.app.sessionInfo.user) {
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

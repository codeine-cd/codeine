angular.module('codeine').directive('projectConfig', ['$rootScope','$scope', '$log', function ($rootScope, $scope, $log) {
    return {
        restrict: 'A',
        scope : {
            project : "="
        },
        link: function (scope, element, attrs) {
            if ($rootScope.app.sessionInfo.permissions.configure_project.indexOf(scope.project) === -1) {
                $log.debug('projectConfig: User can not config project ' + scope.project);
                element.hide();
            } else {
                element.show();
            }
        }

    };

}]);
angular.module('codeine').directive('configureAction', ['$log', function ($log) {
    return {
        restrict: 'A',
        scope : {
            project : '='
        },
        scope: true,
        link: function ($scope, element) {
            if (!$scope.app.sessionInfo.permissions.configure_project.indexOf($scope.project) === -1) {
                $log.debug('configureAction: User do not have configure permissions, will hide element');
                element.hide();
            } else {
                $log.debug('configureAction: User has configure permissions');
                element.show();
            }
        }
    };
}]);
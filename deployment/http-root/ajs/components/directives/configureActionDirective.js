angular.module('codeine').directive('configureAction', ['$log', function ($log) {
    return {
        restrict: 'A',
        scope : {
            project : '=',
            permissions : '='
        },
        link: function ($scope, element) {
            if ($scope.permissions.configure_project.indexOf($scope.project) === -1) {
                $log.debug('configureAction: User do not have configure permissions, will hide element');
                element.hide();
            } else {
                $log.debug('configureAction: User has configure permissions');
                element.show();
            }
        }
    };
}]);
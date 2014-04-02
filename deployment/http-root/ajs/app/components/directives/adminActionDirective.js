'use strict';
angular.module('codeine').directive('adminAction', ['$log', function ($log) {
    return {
        restrict: 'A',
        scope: true,
        link: function ($scope, element) {
            if (!$scope.app.sessionInfo.permissions.administer) {
                $log.debug('adminAction: User is not admin, will hide element');
                element.hide();
            } else {
                $log.debug('adminAction: User is admin');
                element.show();
            }
        }
    };
}]);
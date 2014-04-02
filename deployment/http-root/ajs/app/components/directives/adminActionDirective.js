'use strict';
angular.module('codeine').directive('adminAction', [function () {
    return {
        restrict: 'A',
        scope: true,
        link: function ($scope, element) {
            if (!$scope.app.sessionInfo.permissions.administer) {
                element.hide();
            } else {
                element.show();
            }
        }
    };
}]);
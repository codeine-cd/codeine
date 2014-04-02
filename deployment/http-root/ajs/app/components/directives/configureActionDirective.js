'use strict';
angular.module('codeine').directive('configureAction', [ function () {
    return {
        restrict: 'A',
        scope : {
            project : '=',
            permissions : '='
        },
        link: function ($scope, element) {
            if ($scope.permissions.configure_project.indexOf($scope.project) === -1) {
                element.hide();
            } else {
                element.show();
            }
        }
    };
}]);
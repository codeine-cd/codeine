'use strict';
angular.module('codeine').directive('commandAction', [ function () {
    return {
        restrict: 'A',
        scope : {
            project : '=',
            permissions : '='
        },
        link: function ($scope, element) {
            if ($scope.permissions.command_project.indexOf($scope.project) === -1) {
                element.hide();
            } else {
                element.show();
            }
        }
    };
}]);
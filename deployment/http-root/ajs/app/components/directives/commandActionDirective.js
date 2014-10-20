'use strict';
angular.module('codeine').directive('commandAction', function (LoginService) {
    return {
        restrict: 'A',
        scope : {
            project : '=',
            permissions : '=',
            allowed : '='
        },
        link: function ($scope, element) {
            if (LoginService.getSessionInfo().permissions.command_project.indexOf($scope.project) !== -1 && $scope.allowed) {
                element.show();
            } else {
                element.hide();
            }
        }
    };
});
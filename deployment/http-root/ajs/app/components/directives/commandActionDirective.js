(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandAction(LoginService) {
        return {
            restrict: 'A',
            scope: {
                project: '=',
                allowed: '='
            },
            link: function ($scope, element) {
                if (LoginService.getSessionInfo().permissions.command_project.indexOf($scope.project) !== -1 && $scope.allowed) {
                    element.show();
                } else {
                    element.hide();
                }
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('commandAction',commandAction);

})(angular);
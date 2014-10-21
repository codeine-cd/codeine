(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function configureAction(LoginService) {
        return {
            restrict: 'A',
            scope : {
                project : '=',
                permissions : '='
            },
            link: function ($scope, element) {
                if (LoginService.getSessionInfo().permissions.configure_project.indexOf($scope.project) === -1) {
                    element.hide();
                } else {
                    element.show();
                }
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('configureAction', configureAction);

})(angular);
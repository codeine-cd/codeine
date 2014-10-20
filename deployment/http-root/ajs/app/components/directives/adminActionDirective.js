'use strict';
angular.module('codeine').directive('adminAction', function (LoginService) {
    return {
        restrict: 'A',
        scope: true,
        link: function ($scope, element) {
            if (!LoginService.getSessionInfo().permissions.administer) {
                element.hide();
            } else {
                element.show();
            }
        }
    };
});
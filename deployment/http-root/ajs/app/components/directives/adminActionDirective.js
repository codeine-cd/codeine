(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function adminAction(LoginService) {
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
    }

    //// Angular Code ////
    angular.module('codeine').directive('adminAction', adminAction);


})(angular);
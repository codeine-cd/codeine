'use strict';
angular.module('codeine').directive('commandAction', [ function () {
    return {
        restrict: 'A',
        scope : {
            project : '=',
            permissions : '='
        },
        link: function ($scope, element) {
            if ($scope.permissions) {
                element.show();
            } else {
                element.hide();
            }
        }
    };
}]);
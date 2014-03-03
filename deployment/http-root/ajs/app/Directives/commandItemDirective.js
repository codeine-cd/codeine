angular.module('codeine').directive('commandItem', function () {
    return {
        restrict: 'A',
        scope: {
            commandData : '='
        },
        templateUrl: '/ajs/partials/directives/commandItem.html'
    };
});
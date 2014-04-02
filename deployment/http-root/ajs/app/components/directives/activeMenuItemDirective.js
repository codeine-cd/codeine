'use strict';
angular.module('codeine').directive('activeMenuItem', ['$location', function ($location) {
    return {
        restrict: 'A',
        link: function (scope, element) {
            scope.location = $location;
            scope.$watch('location.absUrl()', function (newPath) {
                var nestedA = (element.is("a")) ? element[0] :  element.find('a')[0];
                var path = nestedA.href;
                if (newPath.indexOf(path) !== -1) {
                    element.addClass('active');
                } else {
                    element.removeClass('active');
                }
            });
        }

    };

}]);
'use strict';
angular.module('codeine').directive('activeMenuItem', ['$location', function ($location) {
    return {
        restrict: 'A',
        link: function (scope, element) {
            scope.location = $location;
            scope.$watch('location.absUrl()', function (newPath) {
                if (newPath.indexOf('?') !== -1 ) {
                    newPath = newPath.substring(0,newPath.indexOf('?'));
                }
                var nestedA = (element.is("a")) ? element[0] :  element.find('a')[0];
                var path = nestedA.href;
                if (newPath === path) {
                    element.addClass('active');
                    element.bind('click',function(e) {
                        e.preventDefault();
                    });
                } else {
                    element.removeClass('active');
                }
            });
        }

    };

}]);
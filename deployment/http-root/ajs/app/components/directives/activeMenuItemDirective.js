(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function activeMenuItem($location, $log) {
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
                    $log.debug("compare 1)" + newPath + " 2)" + path + " res:" + (newPath === path));
                    if (newPath === path) {
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                });
            }

        };

    }

    //// Angular Code ////
    angular.module('codeine').directive('activeMenuItem',activeMenuItem);

})(angular);
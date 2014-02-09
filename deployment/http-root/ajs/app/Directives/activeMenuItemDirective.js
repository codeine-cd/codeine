angular.module('codeine').directive('activeMenuItem', ['$location', function ($location) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var nestedA = (element.is("a")) ? element[0] :  element.find('a')[0];
            var path = nestedA.href;
            scope.location = $location;
            scope.$watch('location.absUrl()', function (newPath) {
                if (path === newPath) {
                    element.addClass('active');
                } else {
                    element.removeClass('active');
                }
            });
        }

    };

}]);
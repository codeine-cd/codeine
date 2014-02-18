angular.module('codeine').directive('threeStateListGroupItem', ['$log', function ($log) {
    return {
        restrict: 'A',
        scope: {
            state : '='
        },
        link: function (scope, element, attrs) {
            scope.click = function() {
                if (!scope.state)
                    scope.state = 0;
                scope.state++;
                scope.state %= 3;
                element.removeClass('list-group-item-info');
                element.removeClass('list-group-item-danger');
                if (scope.state == 1)
                    element.addClass('list-group-item-info');
                if (scope.state == 2)
                    element.addClass('list-group-item-danger');
            }
            element.addClass('list-group-item');
            element.bind('click', scope.click);
        }

    };

}]);
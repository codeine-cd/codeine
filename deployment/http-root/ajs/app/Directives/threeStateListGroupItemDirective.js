angular.module('codeine').directive('threeStateListGroupItem', [ '$animate', function ($animate) {
    return {
        restrict: 'A',
        scope: {
            state : '=',
            onChange: '&'
        },
        link: function (scope, element) {
            scope.click = function() {
                if (scope.state === undefined) {
                    scope.state = 0;
                }
                scope.state++;
                scope.state %= 3;
                $animate.removeClass(element,'list-group-item-info');
                $animate.removeClass(element,'list-group-item-danger');
                if (scope.state == 1) {
                    $animate.addClass(element,'list-group-item-info');
                }
                if (scope.state == 2) {
                    $animate.addClass(element,'list-group-item-danger');
                }
                scope.onChange();
            };
            element.addClass('list-group-item');
            element.bind('click', scope.click);
        }
    };
}]);
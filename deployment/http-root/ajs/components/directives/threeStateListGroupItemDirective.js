angular.module('codeine').directive('threeStateListGroupItem', [ '$animate', function ($animate) {
    return {
        restrict: 'A',
        scope: {
            state : '=',
            onChange: '&'
        },
        link: function (scope, element) {

            function setClass() {
                $animate.removeClass(element,'list-group-item-info');
                $animate.removeClass(element,'list-group-item-danger');
                if (scope.state === 1) {
                    $animate.addClass(element,'list-group-item-info');
                }
                if (scope.state === 2) {
                    $animate.addClass(element,'list-group-item-danger');
                }
            };

            scope.$watch("state",function( newValue, oldValue ) {
                if (newValue !== oldValue) {
                    setClass();
                }
            });

            scope.click = function() {
                if (!scope.state) {
                    scope.state = 0;
                }
                scope.state++;
                scope.state %= 3;

                setClass();
                scope.$digest();
                scope.onChange();
            };
            element.addClass('list-group-item');
            element.bind('click', scope.click);
            if (scope.state) {
                setClass();
            }
        }
    };
}]);
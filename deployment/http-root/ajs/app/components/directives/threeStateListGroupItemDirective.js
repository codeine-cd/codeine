(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function threeStateListGroupItem($animate) {
        return {
            restrict: 'A',
            scope: {
                tag : '=',
                onChange: '&'
            },
            link: function (scope, element) {

                function setClass() {
                    $animate.removeClass(element,'list-group-item-info');
                    $animate.removeClass(element,'list-group-item-danger');
                    if (scope.tag.state === 1) {
                        $animate.addClass(element,'list-group-item-info');
                    }
                    if (scope.tag.state === 2) {
                        $animate.addClass(element,'list-group-item-danger');
                    }
                }

                scope.$watch("tag.state",function( newValue, oldValue ) {
                    if (newValue !== oldValue) {
                        setClass();
                    }
                });

                scope.click = function() {
                    if (!scope.tag.state) {
                        scope.tag.state = 0;
                    }
                    scope.tag.state++;
                    scope.tag.state %= 2;

                    setClass();
                    scope.onChange();
                };
                element.addClass('list-group-item');
                element.bind('click', scope.click);
                if (scope.tag.state) {
                    setClass();
                }
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('threeStateListGroupItem', threeStateListGroupItem);

})(angular);
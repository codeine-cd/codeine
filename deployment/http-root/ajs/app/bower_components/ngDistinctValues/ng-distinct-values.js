angular.module('ngDistinctValues', []);

angular.module('ngDistinctValues').directive('ngDistinctValues',function ($timeout) {

    function setValidityContaining(values) {
        var result = _.groupBy(values, function (v) { return v.element.val(); });
        for (prop in result) {
            if (result[prop].length > 1) {
                _.forEach(result[prop], function (item) {
                    item.ctrl.isValid = false;
                    item.ctrl.$setValidity('distinct', false);
                });
            } else {
                result[prop][0].ctrl.isValid = true;
                result[prop][0].ctrl.$setValidity('distinct', true);
            }
        }
    }
    return {
        require: 'ngModel',
        compile: function (tElem, tAtrrs) {
            var values = [];
            return function link(scope, elem, atrrs, ctrl) {

                // distinct values elements
                values.push({ element: elem, ctrl: ctrl });

                //////////////////////////////////////////////
                // Events
                /////////////////////////////////////////////

                // new item in ng-repeat
                scope.$evalAsync(function () {
                    setValidityContaining(values);
                });

                // Remove Item from array (ng-repeat).
                scope.$on('$destroy', function (scope) {
                    var index = scope.targetScope.$index;
                    values.splice(index, 1);
                    $timeout(function () {
                        setValidityContaining(values);
                    }, 25);

                });

                // When viewValue or ModelValue change
                ctrl.$viewChangeListeners.push(function () {
                    setValidityContaining(values);
                });

                // for outside communication
                elem.on('input', function () {
                    setValidityContaining(values);
                });
            };
        }
    };
});


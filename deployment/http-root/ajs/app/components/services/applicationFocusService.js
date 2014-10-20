(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function ApplicationFocusServiceFactory($window, $log) {

        var isInFocus = true;

        function init() {
            var window = angular.element($window);
            window.focus(function() {
                $log.debug('ApplicationFocusService: got focus');
                isInFocus = true;
            });

            window.blur(function() {
                $log.debug('ApplicationFocusService: no focus');
                isInFocus = false;
            });
        }

        return {
            isInFocus : function() {return isInFocus;},
            init : init
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('ApplicationFocusService',ApplicationFocusServiceFactory);

})(angular);
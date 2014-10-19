(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function AlertServiceFactory($rootScope, $timeout,$injector) {
        var Alert = this;
        var alerts = $rootScope.alerts = [];
        var currentID = 1;

        Alert.close = function (alert, index) {
            if (alert.timer !== null)  {
                $timeout.cancel(alert.timer);
            }
            alerts.splice(index, 1);
        };

        Alert.remove = function (alert, index) {
            if (alert.timer !== null) {
                $timeout.cancel(alert.timer);
            }
            $('#' + index).remove();
        };

        var createAlert = function(type, message) {
            var alert = { type: type, msg: message, close: Alert.close, remove: Alert.remove };
            return alert;
        };

        Alert.addAlert = function (type, message, time) {
            var alert = createAlert(type, message);
            if ((time === undefined) || (time === null)) {
                time = 3000;
            }
            if (time !== null) {
                alert.timer = $timeout(function () {
                    alerts.splice(alerts.indexOf(alert), 1);
                }, time);
            }
            alerts.push(alert);
        };

        Alert.addCompiledAlert = function (type, message, scope, time) {
            var alert = createAlert(type, message, time);
            var html = "<alert id='confirm" + currentID + "' type='alert.type' close='alert.remove(alert, \"confirm" + currentID + "\")'>" + alert.msg + "</alert>";
            currentID++;
            var elm = angular.element(html);
            var $compile = $injector.get('$compile');
            var compiled = $compile(elm);
            scope.alert = alert;
            $('#toast-container').append(compiled(scope));
        };

        return Alert;
    }


    //// Angular Code ////
    angular.module('codeine').factory('AlertService', AlertServiceFactory);

})(angular);
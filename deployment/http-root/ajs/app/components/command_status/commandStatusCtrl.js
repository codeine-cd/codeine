(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandStatusCtrl($scope, $log,$routeParams, CodeineService, commandStatus, $interval, $timeout, ApplicationFocusService) {
        $scope.projectName = $routeParams.project_name;
        $scope.commandStatus = commandStatus;

        var maxUpdatesNotInFocus = 100;
        var intervalTriggered = 0;
        var interval = $interval(function() {
            if (!ApplicationFocusService.isInFocus && intervalTriggered > maxUpdatesNotInFocus) {
                return;
            }
            intervalTriggered++;
            CodeineService.getCommandStatus($scope.projectName, $routeParams.command_id).success(function(data) {
                var scrolledToBottom = $(window).scrollTop() + $(window).height() > $(document).height() - 100;
                $scope.commandStatus = data;
                if ($scope.commandStatus.finished) {
                    $log.debug('commandStatusCtrl: command is finished');
                    $timeout(function() {
                        $interval.cancel(interval);
                    });
                }
                if (scrolledToBottom) {
                    $timeout(function() {
                        $(document).scrollTop($(document).height());
                    });
                }
            });
        },5000);

        $scope.$on('$destroy', function() {
            if (interval) {
                $interval.cancel(interval);
            }
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandStatusCtrl', commandStatusCtrl);

})(angular);
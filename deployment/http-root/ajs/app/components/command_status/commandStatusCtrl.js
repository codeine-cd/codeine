'use strict';
angular.module('codeine').controller('commandStatusCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'commandStatus','$interval','$timeout',
    function($scope, $log,$routeParams, CodeineService, commandStatus, $interval, $timeout) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('commandStatusCtrl: command status is ' + angular.toJson(commandStatus));
    $scope.commandStatus = commandStatus;

    var interval = $interval(function() {
        if (!$scope.app.isInFocus) {
            return;
        }
        CodeineService.getCommandStatus($scope.projectName, $routeParams.command_id).success(function(data) {
            var scrolledToBottom = $(window).scrollTop() + $(window).height() > $(document).height() - 100;
            $scope.commandStatus = data;
            if ($scope.commandStatus.finished) {
                $log.debug('commandStatusCtrl: command is finished');
                $interval.cancel(interval);
            }
            if (scrolledToBottom) {
                $timeout(function() {
                    $(document).scrollTop($(document).height());
                });
            }
        });
    },5000);

    $scope.getDateForView = function(milli){
        return new Date(milli).toLocaleString();
    }

    $scope.$on('$destroy', function() {
        if (interval) {
            $interval.cancel(interval);
        }
    });
}]);
'use strict';
angular.module('codeine').directive('commandItem',['CodeineService','AlertService','$window', function (CodeineService, AlertService, $window) {
    return {
        restrict: 'A',
        scope: {
            commandData : '='
        },
        templateUrl: '/components/directives/command_item/commandItem.html',
        link: function ($scope) {

            $scope.cancelCommand =function($event) {
                $event.stopPropagation();
                if ($window.confirm('Are you sure you would like to cancel the command?')) {
                    CodeineService.cancelCommand($scope.commandData.project, $scope.commandData.id).success(function () {
                        AlertService.addAlert('success', 'Command was canceled successfully');
                    });
                }
            };
        }
    };
}]);
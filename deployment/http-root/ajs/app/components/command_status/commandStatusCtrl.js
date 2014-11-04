(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandStatusCtrl($scope, $log,$routeParams, CodeineService, commandStatus, $interval, $timeout, ApplicationFocusService, $location, SelectedNodesService) {
        /*jshint validthis:true */
        var vm = this;

        vm.projectName = $routeParams.project_name;
        vm.commandStatus = commandStatus;

        var maxUpdatesNotInFocus = 100;
        var intervalTriggered = 0;
        var interval = $interval(function() {
            if (!ApplicationFocusService.isInFocus && intervalTriggered > maxUpdatesNotInFocus) {
                return;
            }
            intervalTriggered++;
            CodeineService.getCommandStatus(vm.projectName, $routeParams.command_id).success(function(data) {
                var scrolledToBottom = $(window).scrollTop() + $(window).height() > $(document).height() - 100;
                vm.commandStatus = data;
                if (vm.commandStatus.finished) {
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

        vm.rerunCommand = function() {
            $log.debug('commandStatusCtrl: will rerun the command - ' + vm.commandStatus.command);
            var url = '/codeine/project/' + vm.projectName + '/command/' + vm.commandStatus.command + '/setup';
            SelectedNodesService.setSelectedNodes(vm.commandStatus.nodes_list, url, vm.commandStatus.params);
            $location.path(url);
        };

        $scope.$on('$destroy', function() {
            if (interval) {
                $interval.cancel(interval);
            }
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandStatusCtrl', commandStatusCtrl);

})(angular);
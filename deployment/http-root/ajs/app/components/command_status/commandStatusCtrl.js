(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandStatusCtrl($scope, $log,$routeParams, CodeineService, commandStatus, $interval, $timeout, ApplicationFocusService, $location, SelectedNodesService, $window, AlertService, project) {
        /*jshint validthis:true */
        var vm = this;
        vm.project = project;
        vm.projectName = $routeParams.project_name;
        vm.tabName = $routeParams.tab_name;
        vm.commandStatus = commandStatus;
        var maxUpdatesNotInFocus = 100;
        vm.all_nodes_is_open = vm.commandStatus.nodes_list.length < 6;
        vm.fail_nodes_is_open = vm.commandStatus.fail_list.length < 6;
        var intervalTriggered = 0;
        var interval = $timeout(refreshCommands, 5000);

        vm.rerunCommand = function() {
            $log.debug('commandStatusCtrl: will rerun the command - ' + vm.commandStatus.command);
            var url = '/codeine/view/' + vm.tabName + '/project/' + vm.projectName + '/command/' + vm.commandStatus.command + '/setup';
            SelectedNodesService.setSelectedNodes(vm.commandStatus.nodes_list, url, vm.commandStatus.params);
            $location.path(url);
        };

        vm.cancelCommand =function($event) {
            $event.stopPropagation();
            if ($window.confirm('Are you sure you would like to cancel the command?')) {
                CodeineService.cancelCommand(vm.projectName, $routeParams.command_id).success(function () {
                    AlertService.addAlert('success', 'Command was canceled successfully');
                });
            }
        };

      function refreshCommands() {
        if (!ApplicationFocusService.isInFocus && intervalTriggered > maxUpdatesNotInFocus) {
          interval = $timeout(refreshCommands, 5000);
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
          interval = $timeout(refreshCommands, 5000);
        });
      }

        $scope.$on('$destroy', function() {
            if (interval) {
              $timeout.cancel(interval);
            }
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandStatusCtrl', commandStatusCtrl);

})(angular);
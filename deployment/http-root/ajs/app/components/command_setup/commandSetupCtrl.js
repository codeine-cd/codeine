(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandSetupCtrl($log, SelectedNodesService, $location, $routeParams,command,CodeineService) {
        /*jshint validthis:true */
        var vm = this;

        vm.command = command;
        vm.nodes = SelectedNodesService.getSelectedNodes($location.path());

        for (var i=0; i <vm.command.parameters.length; i++) {
            vm.command.parameters[i].value = vm.command.parameters[i].default_value;
        }

        var predefinedParams = SelectedNodesService.getSelectedParams($location.path());
        for (var i1=0; i1 < predefinedParams.length; i1++) {
            for (var j=0; j < vm.command.parameters.length; j++) {
                if (vm.command.parameters[j].name === predefinedParams[i1].name) {
                    vm.command.parameters[j].value = predefinedParams[i1].value;
                    break;
                }
            }
        }

        vm.projectName = $routeParams.project_name;
        vm.commandName = $routeParams.command_name;
        if (vm.nodes.length === 1) {
            vm.command.command_strategy = 'Single';
            vm.command.prevent_override = true;
        }

        vm.runCommand = function() {
            $log.debug('commandSetupCtrl: will run the command - ' + angular.toJson(vm.command) + ' on ' + vm.nodes.length + ' nodes');
            CodeineService.runCommand(vm.command,vm.nodes).success(function(data) {
                $log.debug('commandSetupCtrl: Command executed, result is ' + angular.toJson(data));
                $location.path('/codeine/project/' + vm.projectName + '/command/' + vm.commandName + '/' + data + '/status');
            });
        };

        vm.validateParameter = function(value,index) {
            if (!vm.command.parameters[index].validation_expression) {
                return true;
            }
            $log.debug('commandSetupCtrl: ' + value + ' - ' + vm.command.parameters[index].validation_expression);
            var regexp = new RegExp(vm.command.parameters[index].validation_expression);
            return regexp.test(value);
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandSetupCtrl',commandSetupCtrl);

})(angular);
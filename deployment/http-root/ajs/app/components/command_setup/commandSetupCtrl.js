(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function getParsedParamValue(type, stringValue) {
        if (type === 'Boolean') {
            return 'true' === stringValue;
        } else {
            return stringValue;
        }
    }

    function commandSetupCtrl($log, SelectedNodesService, $location, $routeParams,command,CodeineService,AlertService) {
        /*jshint validthis:true */
        var vm = this;

        vm.command = command;
        vm.projectName = $routeParams.project_name;
        vm.tabName = $routeParams.tab_name;
        vm.commandName = $routeParams.command_name;
        vm.nodes = SelectedNodesService.getSelectedNodes($location.path());
        if (vm.nodes === undefined) {
            $log.error('did not find selected nodes');
            AlertService.addAlert('danger', "Could not setup the command at this time");
            vm.is404 = true;
            return;
        }

        for (var i=0; i <vm.command.parameters.length; i++) {
            vm.command.parameters[i].value = getParsedParamValue(vm.command.parameters[i].type, vm.command.parameters[i].default_value);
        }

        var predefinedParams = SelectedNodesService.getSelectedParams($location.path());
        for (var i1=0; i1 < predefinedParams.length; i1++) {
            for (var j=0; j < vm.command.parameters.length; j++) {
                if (vm.command.parameters[j].name === predefinedParams[i1].name) {
                    vm.command.parameters[j].value = getParsedParamValue(vm.command.parameters[j].type, predefinedParams[i1].value);
                    break;
                }
            }
        }

        if (vm.nodes.length === 1) {
            vm.command.command_strategy = 'Single';
            vm.command.prevent_override = true;
        }
        vm.nodesIsOpen = vm.nodes.length < 6;

        vm.runCommand = function() {
            $log.debug('commandSetupCtrl: will run the command - ' + angular.toJson(vm.command) + ' on ' + vm.nodes.length + ' nodes');
            vm.runPromise = CodeineService.runCommand(vm.command,vm.nodes).success(function(data) {
                $log.debug('commandSetupCtrl: Command executed, result is ' + angular.toJson(data));
                $location.path('/codeine/view/' + vm.tabName + '/project/' + vm.projectName + '/command/' + vm.commandName + '/' + data + '/status');
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

        vm.nodeNameValidate = function() {
            if (vm.nodes.length > 1) {
                return vm.node_name_retype == vm.nodes.length || !vm.command.safe_guard;
            } else {
                return vm.node_name_retype === vm.nodes[0].alias || !vm.command.safe_guard;
            }
        };
        vm.getRetypeTitle = function() {
            if (vm.nodes.length > 1) {
                return 'Re-type number of nodes';
            } else {
                return 'Re-type node name';
            }
        };
        vm.nodes_per_minute = function() {
            return (vm.nodes.length / vm.command.duration);
        };
        vm.nodes_per_minute_for_view = function() {
            return vm.nodes_per_minute() >= 1 ? parseInt(vm.nodes_per_minute()) : parseInt(1 / vm.nodes_per_minute());
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandSetupCtrl',commandSetupCtrl);

})(angular);
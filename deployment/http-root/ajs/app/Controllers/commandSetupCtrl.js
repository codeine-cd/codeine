angular.module('codeine').controller('commandSetupCtrl',['$scope', '$log','SelectedNodesService','$location','$routeParams','command','$animate',
    function($scope, $log, SelectedNodesService, $location, $routeParams,command,$animate) {
        $scope.command = command;
        $scope.nodes = SelectedNodesService.getSelectedNodes($location.path());
        $log.debug('commandSetupCtrl: created for command ' + angular.toJson(command));
        $log.debug('commandSetupCtrl: nodes are :' + $scope.nodes);

        for (var i=0; i <$scope.command.parameters.length; i++) {
            $scope.command.parameters[i].value = $scope.command.parameters[i].default_value;
        }

        $scope.projectName = $routeParams.project_name;
        $scope.commandName = $routeParams.command_name;

        $scope.validateParameter = function(value,index) {
            if (!$scope.command.parameters[index].validation_expression) {
                return true;
            }
            $log.debug('commandSetupCtrl: ' + value + ' - ' + $scope.command.parameters[index].validation_expression);
            var regexp = new RegExp($scope.command.parameters[index].validation_expression);
            var res = regexp.test(value);
            if (res) {
                $animate.removeClass($('#' + $scope.command.parameters[index].name),'has-error');
            } else {
                $animate.addClass($('#' + $scope.command.parameters[index].name),'has-error');
            }
            return res;
        };

    }]);
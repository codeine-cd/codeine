angular.module('codeine').controller('commandSetupCtrl',['$scope', '$log','SelectedNodesService','$location','$routeParams','command','$animate','CodeineService',
    function($scope, $log, SelectedNodesService, $location, $routeParams,command,$animate,CodeineService) {
        $scope.command = command;
        $scope.nodes = SelectedNodesService.getSelectedNodes($location.path());
        $log.debug('commandSetupCtrl: created for command ' + angular.toJson(command));
        $log.debug('commandSetupCtrl: nodes are :' + $scope.nodes);

        for (var i=0; i <$scope.command.parameters.length; i++) {
            $scope.command.parameters[i].value = $scope.command.parameters[i].default_value;
        }

        $scope.projectName = $routeParams.project_name;
        $scope.commandName = $routeParams.command_name;

        $scope.runCommand = function() {
            $log.debug('commandSetupCtrl: will run the command - ' + angular.toJson($scope.command) + ' on ' + $scope.nodes.length + ' nodes');
            CodeineService.runCommand($scope.command,$scope.nodes).success(function(data) {
                $log.debug('commandSetupCtrl: Command executed, result is ' + angular.toJson(data));
                $location.path('/codeine/project/' + $scope.projectName + '/command/' + data + '/status');
            });
        };

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
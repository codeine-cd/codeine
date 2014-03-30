angular.module('codeine').controller('commandSetupCtrl',['$scope', '$log','SelectedNodesService','$location','$routeParams','command','CodeineService',
    function($scope, $log, SelectedNodesService, $location, $routeParams,command,CodeineService) {
        $scope.command = command;
        $scope.nodes = SelectedNodesService.getSelectedNodes($location.path());
        $log.debug('commandSetupCtrl: created for command ' + angular.toJson(command) + ' on ' + $scope.nodes.length + ' nodes');

        for (var i=0; i <$scope.command.parameters.length; i++) {
            $scope.command.parameters[i].value = $scope.command.parameters[i].default_value;
        }

        $scope.projectName = $routeParams.project_name;
        $scope.commandName = $routeParams.command_name;

        $scope.runCommand = function() {
            $log.debug('commandSetupCtrl: will run the command - ' + angular.toJson($scope.command) + ' on ' + $scope.nodes.length + ' nodes');
            CodeineService.runCommand($scope.command,$scope.nodes).success(function(data) {
                $log.debug('commandSetupCtrl: Command executed, result is ' + angular.toJson(data));
                $location.path('/codeine/project/' + $scope.projectName + '/command/' + $scope.commandName + '/' + data + '/status');
            });
        };

        $scope.validateParameter = function(value,index) {
            if (!$scope.command.parameters[index].validation_expression) {
                return true;
            }
            $log.debug('commandSetupCtrl: ' + value + ' - ' + $scope.command.parameters[index].validation_expression);
            var regexp = new RegExp($scope.command.parameters[index].validation_expression);
            return regexp.test(value);
        };

    }]);
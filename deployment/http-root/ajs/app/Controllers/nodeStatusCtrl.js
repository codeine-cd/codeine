angular.module('codeine').controller('nodeStatusCtrl',['$scope', '$log', '$routeParams', '$location', 'CodeineService', 'nodeStatus', 'projectConfiguration', 'SelectedNodesService',
    function($scope, $log,$routeParams, $location, CodeineService, nodeStatus, projectConfiguration, SelectedNodesService ) {
        $scope.projectName = $routeParams.project_name;
        $scope.nodeStatus = nodeStatus;
        $scope.projectConfiguration= projectConfiguration;
        $log.debug('nodeStatusCtrl: current project is ' + $scope.projectName);
        $log.debug('nodeStatusCtrl: node status ' + angular.toJson($scope.nodeStatus));
        $log.debug('nodeStatusCtrl: project configuration ' + angular.toJson($scope.projectConfiguration));
        $scope.nodeStatus = nodeStatus;

        $scope.runCommand = function(command) {
            $log.debug('projectStatusCtrl: will run command ' + command);
            var url = '/codeine/project/' + $scope.projectName + '/command/' + command + '/setup';
            var nodes = [];
            nodes.push($scope.nodeStatus);
            SelectedNodesService.setSelectedNodes(nodes,url);
            $location.path(url);
        };
}]);
(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function nodeStatusCtrl($scope, $log,$routeParams, $location, nodeStatus, projectConfiguration, SelectedNodesService, projects) {
        $scope.projectName = $routeParams.project_name;
        $scope.nodeStatus = nodeStatus;
        $scope.projectConfiguration= projectConfiguration;
        $log.debug('nodeStatusCtrl: current project is ' + $scope.projectName);
        $log.debug('nodeStatusCtrl: node status ' + angular.toJson($scope.nodeStatus));
        $log.debug('nodeStatusCtrl: project configuration ' + angular.toJson($scope.projectConfiguration));
        $scope.nodeStatus = nodeStatus;
        $scope.projectConfiguration.commands_include_inherited = [];
        $scope.projectConfiguration.commands_include_inherited = $scope.projectConfiguration.commands_include_inherited.concat($scope.projectConfiguration.commands);
        angular.forEach(projects, function(key) {
            if ($scope.projectConfiguration.projectConfiguration.indexOf(key.name) !== -1) {
                $scope.projectConfiguration.commands_include_inherited = $scope.projectConfiguration.commands_include_inherited.concat(key.commands);
            }
        });

        $scope.runCommand = function(command) {
            $log.debug('projectStatusCtrl: will run command ' + command);
            var url = '/codeine/project/' + $scope.projectName + '/command/' + command + '/setup';
            var nodes = [];
            nodes.push($scope.nodeStatus);
            SelectedNodesService.setSelectedNodes(nodes,url);
            $location.path(url);
        };
    }

    //// Angular Code ////

    angular.module('codeine').controller('nodeStatusCtrl', nodeStatusCtrl);

})(angular);
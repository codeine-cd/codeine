(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function nodeStatusCtrl($scope, $log,$routeParams, $location, nodeStatus, projectConfiguration, SelectedNodesService, commands) {
        $scope.projectName = $routeParams.project_name;
        $scope.nodeStatus = nodeStatus;
        $scope.projectConfiguration= projectConfiguration;
        //$log.debug('nodeStatusCtrl: node status ' + angular.toJson($scope.nodeStatus));
        //$log.debug('nodeStatusCtrl: project configuration ' + angular.toJson($scope.projectConfiguration));
        $scope.nodeStatus = nodeStatus;
        $scope.commands = commands;

        $scope.runCommand = function(command) {
            $log.debug('projectStatusCtrl: will run command ' + command);
            var url = '/codeine/project/' + $scope.projectName + '/command/' + command + '/setup';
            var nodes = [];
            nodes.push($scope.nodeStatus);
            SelectedNodesService.setSelectedNodes(nodes,url);
            $location.path(url);
        };

        $scope.isEmpty = function(o) {
            for(var p in o) {
                if (o.hasOwnProperty(p)) {
                    return false;
                }
            }
            return true;
        };
    }

    //// Angular Code ////

    angular.module('codeine').controller('nodeStatusCtrl', nodeStatusCtrl);

})(angular);
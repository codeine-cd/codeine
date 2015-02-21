(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function nodeStatusCtrl($scope, $log,$routeParams, $location, nodeStatus, project, SelectedNodesService) {
        $scope.projectName = $routeParams.project_name;
        $scope.tabName = $routeParams.tab_name;
        $scope.nodeStatus = nodeStatus;
        $scope.projectConfiguration = project.configuration;
        //$log.debug('nodeStatusCtrl: node status ' + angular.toJson($scope.nodeStatus));
        //$log.debug('nodeStatusCtrl: project configuration ' + angular.toJson($scope.projectConfiguration));
        $scope.commands = project.runnableCommands;

        $scope.runCommand = function(command) {
            $log.debug('projectStatusCtrl: will run command ' + command);
            var url = '/codeine/view/' + $scope.tabName + '/project/' + $scope.projectName + '/command/' + command + '/setup';
            var nodes = [];
            nodes.push($scope.nodeStatus);
            SelectedNodesService.setSelectedNodes(nodes,url);
            $location.path(url);
        };

        $scope.getCollectorDescription = function(collectorName){
            for (var i3=0; i3 < $scope.projectConfiguration.collectors.length ; i3++) {
                if ($scope.projectConfiguration.collectors[i3].name === collectorName) {
                    return $scope.projectConfiguration.collectors[i3].description;
                }
            }
            return '';
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
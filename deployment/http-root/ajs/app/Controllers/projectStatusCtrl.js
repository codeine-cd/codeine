angular.module('codeine').controller('projectStatusCtrl',['$scope', '$log', '$routeParams', 'projectConfiguration', 'projectStatus', function($scope, $log,$routeParams, projectConfiguration, projectStatus ) {
    $scope.projectName = $routeParams.project_name;
    $scope.projectConfiguration= projectConfiguration;
    $scope.projectStatus = projectStatus;
    $scope.selectedMonitor = 'All Nodes';
    $scope.maxTags = 10;
    $scope.filteredNodes = [];
    $scope.versionIsOpen = [];
    $scope.allNodesCount = 0;
    $log.debug('projectStatusCtrl: current project is ' + $scope.projectName);
    $log.debug('projectStatusCtrl: projectConfiguration = ' + angular.toJson(projectConfiguration));
    $log.debug('projectStatusCtrl: projectStatus = ' + angular.toJson(projectStatus));

    for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
        $scope.allNodesCount += $scope.projectStatus.nodes_for_version[i].nodes.length;
    };

    var initNodesLimit = function() {
        $scope.nodesLimit = [];
        $scope.nodesVisible = [];
        for (var i=0; i < $scope.projectStatus.nodes_for_version.length ; i++) {
            $scope.nodesLimit[i] = 10;
            $scope.nodesVisible[i] = 0;
        }
    };

    initNodesLimit();

    $scope.$watch("nodesFilter",function(newName, oldName) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('projectStatusCtrl: nodesFilter was changed');
            for (var i=0; i < $scope.projectStatus.nodes_for_version.length ; i++)  {
                $scope.nodesVisible[i] = 0;
            }
        }
    );

    $scope.isVersionVisible = function(nodes) {
        if (!nodes) {
            return true;
        }
        for (var i=0; i < nodes.length ; i++) {
            if((nodes[i].visible === undefined) || (nodes[i].visible)) {
                return true;
            }
        }
        return false;
    };

    $scope.loadMoreNodes = function(index) {
        $scope.nodesLimit[index] += 10;
    };

    $scope.selectMonitor = function(monitor) {
        $scope.selectedMonitor = monitor;
    };

    $scope.checkboxClick = function(versionItem, event) {
        event.stopPropagation();
        angular.forEach(versionItem.nodes, function(item) {
           item.checked = event.target.checked;
        });
    };

    $scope.isAllNodesChecked = function() {
        for (var i=0 ; i < projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < projectStatus.nodes_for_version[i].nodes.length; j++) {
                if (!projectStatus.nodes_for_version[i].nodes[j].checked)
                    return false;
            }
        }
        return true;
    };

    $scope.isVersionChecked = function(versionItem) {
        for (var i=0 ; i < versionItem.nodes.length; i++) {
            if (!versionItem.nodes[i].checked)
            return false;
        }
        return true;
    };

    $scope.doSelectAllNodes = function(event) {
        event.stopPropagation();
        angular.forEach(projectStatus.nodes_for_version, function(versionItem) {
            angular.forEach(versionItem.nodes, function(node) {
                node.checked = event.target.checked;
            });
        });
    };
}]);
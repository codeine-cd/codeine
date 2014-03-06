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

    var moveNodeToVisible = function(versionItem,node) {
        node.visible = true;
        if (!versionItem.visibleNodes) {
            versionItem.visibleNodes = [];
        }
        versionItem.visibleNodes.push(node);
    }

    for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
        $scope.allNodesCount += $scope.projectStatus.nodes_for_version[i].nodes.length;
    };

    for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
        $scope.projectStatus.nodes_for_version[i].filteredNodes = $scope.projectStatus.nodes_for_version[i].nodes.slice();
        moveNodeToVisible($scope.projectStatus.nodes_for_version[i],$scope.projectStatus.nodes_for_version[i].filteredNodes[0]);
    }

    $scope.$watch("nodesFilter",function( newName, oldName ) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('projectStatusCtrl: nodesFilter was changed')
            for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
                $scope.projectStatus.nodes_for_version[i].filteredNodes.splice(0,$scope.projectStatus.nodes_for_version[i].filteredNodes.length);
                $scope.projectStatus.nodes_for_version[i].visibleNodes.splice(0,$scope.projectStatus.nodes_for_version[i].visibleNodes.length);
                for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].nodes.length; j++) {
                    $scope.projectStatus.nodes_for_version[i].nodes[j].visible = false;
                    if (isNodeFiltered($scope.projectStatus.nodes_for_version[i].nodes[j])) {
                        $scope.projectStatus.nodes_for_version[i].filteredNodes.push($scope.projectStatus.nodes_for_version[i].nodes[j]);
                    }
                }
                if ($scope.projectStatus.nodes_for_version[i].filteredNodes.length > 0) {
                    moveNodeToVisible($scope.projectStatus.nodes_for_version[i],$scope.projectStatus.nodes_for_version[i].filteredNodes[0]);
                }
            }
        }
    );

    // Returns true if the node should be in the filtered array (Displayed)
    var isNodeFiltered = function(node) {
        return ((!$scope.nodesFilter) || (node.node_name.indexOf($scope.nodesFilter) !== -1));
    };



    $scope.loadMoreNodes = function(index) {
        var j = 0;
        if ($scope.projectStatus.nodes_for_version[index].filteredNodes.length === $scope.projectStatus.nodes_for_version[index].visibleNodes.length) {
            return;
        }
        for (var i=0; (i < $scope.projectStatus.nodes_for_version[index].filteredNodes.length) && (j < 10); i++) {
            if (!$scope.projectStatus.nodes_for_version[index].filteredNodes[i].visible) {
                moveNodeToVisible($scope.projectStatus.nodes_for_version[index],$scope.projectStatus.nodes_for_version[index].filteredNodes[i]);
                j++;
            }
        }
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
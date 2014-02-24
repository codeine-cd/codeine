angular.module('codeine').controller('projectStatusCtrl',['$scope', '$log', '$routeParams', 'projectConfiguration', 'projectStatus', function($scope, $log,$routeParams, projectConfiguration, projectStatus ) {
    $scope.projectName = $routeParams.project_name;
    $scope.projectConfiguration= projectConfiguration;
    $scope.projectStatus = projectStatus;
    $scope.selectedMonitor = 'All Nodes';
    $scope.maxTags = 10;
    $log.debug('projectStatusCtrl: current project is ' + $scope.projectName);
    $log.debug('projectStatusCtrl: projectConfiguration = ' + angular.toJson(projectConfiguration));
    $log.debug('projectStatusCtrl: projectStatus = ' + angular.toJson(projectStatus));
    $scope.filteredNodes = [];

    $scope.allNodesCount = 0;
    for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
        $scope.allNodesCount += $scope.projectStatus.nodes_for_version[i].nodes.length;
    }

    $scope.showAllTags = function() {
        $scope.maxTags = 99999;
    }

    $scope.selectMonitor = function(monitor) {
        $scope.selectedMonitor = monitor;
    }

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
    }

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
angular.module('codeine').controller('projectStatusCtrl',['$scope', '$log', '$routeParams', 'projectConfiguration', 'projectStatus', '$filter','$location','SelectedNodesService', function($scope, $log,$routeParams, projectConfiguration, projectStatus, $filter, $location, SelectedNodesService) {
    $scope.projectName = $routeParams.project_name;
    $scope.projectConfiguration= projectConfiguration;
    $scope.projectStatus = projectStatus;
    $scope.selectedMonitor = 'All Nodes';
    $scope.maxTags = 10;
    $scope.filteredNodes = [];
    $scope.versionIsOpen = [];
    $scope.allNodesCount = 0;
    $scope.nodesFilter = '';
    $log.debug('projectStatusCtrl: current project is ' + $scope.projectName);
    $log.debug('projectStatusCtrl: projectConfiguration = ' + angular.toJson(projectConfiguration));
    $log.debug('projectStatusCtrl: projectStatus = ' + angular.toJson(projectStatus));

    $scope.initFromQueryString = function(queryStringObject) {
        var shouldRefresh = false;
        if (angular.isDefined(queryStringObject.monitorFilter)) {
            shouldRefresh = true;
            $log.debug('projectStatusCtrl: Monitor filter init from query string - ' + queryStringObject.monitorFilter);
            $scope.selectedMonitor = queryStringObject.monitorFilter;
        }
        if (angular.isDefined(queryStringObject.tagsOn)) {
            shouldRefresh = true;
            $log.debug('projectStatusCtrl: Tags on init from query string - ' + queryStringObject.tagsOn);
            var array = queryStringObject.tagsOn.split(',');
            for (var i=0; i < array.length; i++) {
                for (var j=0; i < $scope.projectStatus.tag_info.length ; j++) {
                    if ($scope.projectStatus.tag_info[j].name === array[i]) {
                        $scope.projectStatus.tag_info[j].state = 1;
                    }
                }
            }
        }
        if (angular.isDefined(queryStringObject.tagsOff)) {
            shouldRefresh = true;
            $log.debug('projectStatusCtrl: Tags on init from query string - ' + queryStringObject.tagsOff);
            var array = queryStringObject.tagsOff.split(',');
            for (var i=0; i < array.length; i++) {
                for (var j=0; i < $scope.projectStatus.tag_info.length ; j++) {
                    if ($scope.projectStatus.tag_info[j].name === array[i]) {
                        $scope.projectStatus.tag_info[j].state = 2;
                    }
                }
            }
        }
        return shouldRefresh;
    };

    if ($scope.initFromQueryString($location.search())) {
        $scope.refreshFilters();
    }

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
        for (var j=0; j < 10 && j < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; j++) {
            moveNodeToVisible($scope.projectStatus.nodes_for_version[i],$scope.projectStatus.nodes_for_version[i].filteredNodes[j]);
        }
    }

    $scope.$watch("selectedMonitor",function( newName, oldName ) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('projectStatusCtrl: selectedMonitor was changed')
            $location.search('monitorFilter',newName);
            $scope.refreshFilters();
        }
    );

    $scope.$watch("nodesFilter",function( newName, oldName ) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('projectStatusCtrl: nodesFilter was changed');
            $scope.refreshFilters();
        }
    );

    $scope.updateTags = function() {
        $log.debug('projectStatusCtrl: tags were changed');
        var on = [], off = [];
        for (var i=0; i < $scope.projectStatus.tag_info.length ; i++) {
            if ($scope.projectStatus.tag_info[i].state === 1) {
                on.push($scope.projectStatus.tag_info[i].name);
            } else if ($scope.projectStatus.tag_info[i].state === 2) {
                off.push($scope.projectStatus.tag_info[i].name);
            }
        }
        $scope.$apply(function() {
            $location.search('tagsOn',on.join(','));
            $location.search('tagsOff',off.join(','));
        });
        $scope.refreshFilters();
    };

    $scope.refreshFilters = function() {
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
                for (var k=0; k < 10 && k < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; k++) {
                    moveNodeToVisible($scope.projectStatus.nodes_for_version[i],$scope.projectStatus.nodes_for_version[i].filteredNodes[k]);
                }
            }
        }
    };

    // Returns true if the node should be in the filtered array (Displayed)
    var isNodeFiltered = function(node) {
        return $filter('nodeFilter')(node,$scope.nodesFilter,$scope.selectedMonitor,$scope.projectStatus.tag_info);
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

    $scope.runCommand = function(command) {
        $log.debug('projectStatusCtrl: will run command ' + command);
        var url = '/codeine/project/' + $scope.projectName + '/command/' + command + '/setup';
        SelectedNodesService.setSelectedNodes($scope.getAllSelectedNodes(),url);
        $location.path(url);
    };

    $scope.checkboxClick = function(versionItem, event) {
        event.stopPropagation();
        angular.forEach(versionItem.nodes, function(item) {
           item.checked = event.target.checked;
        });
    };

    $scope.isAnyNodeChecked = function() {
        for (var i=0 ; i < projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < projectStatus.nodes_for_version[i].nodes.length; j++) {
                if (projectStatus.nodes_for_version[i].nodes[j].checked) {
                    return true;
                }
            }
        }
        return false;
    };

    $scope.getAllSelectedNodes = function() {
        var res = [];
        for (var i=0 ; i < projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < projectStatus.nodes_for_version[i].nodes.length; j++) {
                if (projectStatus.nodes_for_version[i].nodes[j].checked) {
                    res.push(projectStatus.nodes_for_version[i].nodes[j]);
                }
            }
        }
        return res;
    }

    $scope.isAllNodesChecked = function() {
        for (var i=0 ; i < projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < projectStatus.nodes_for_version[i].nodes.length; j++) {
                if (!projectStatus.nodes_for_version[i].nodes[j].checked) {
                    return false;
                }
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
'use strict';
angular.module('codeine').controller('projectStatusCtrl',['$scope','$rootScope','$log','$filter','$location','SelectedNodesService','Constants', function($scope,$rootScope,$log,$filter,$location,SelectedNodesService,Constants) {
    $scope.projectName = $scope.projectConfiguration.name;
    $scope.allNodesCount = 0;
    $log.debug('projectStatusCtrl: current project is ' + $scope.projectName);
    $log.debug('projectStatusCtrl: projectConfiguration = ' + angular.toJson($scope.projectConfiguration));
    $log.debug('projectStatusCtrl: projectStatus = ' + angular.toJson($scope.projectStatus));

    $scope.versionIsOpen = [];
    $scope.maxNodesToShow = 2;
    for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
        $scope.versionIsOpen[i] = true;
    }
    $scope.collapseAll = function() {
        for (var i=0 ; i < $scope.versionIsOpen.length; i++) {
            $scope.versionIsOpen[i] = false;
        }
    };
    $scope.expandAll = function() {
        for (var i=0 ; i < $scope.versionIsOpen.length; i++) {
            $scope.versionIsOpen[i] = true;
        }
    };
    $scope.shouldShowClearFilters = function() {
        var search = $location.search();
        var noTagsOn = ((!angular.isDefined(search.tagsOn) || search.tagsOn.length === 0));
        var noTagsOff = ((!angular.isDefined(search.tagsOff) || search.tagsOff.length === 0));
        var noTags = noTagsOn && noTagsOff;
        return !(($scope.selectedMonitor === 'All Nodes') && ($scope.nodesFilter === '') && (noTags));
    };

    $scope.initValues = function() {
        $scope.selectedMonitor = 'All Nodes';
        $scope.nodesFilter = '';
    };

    $scope.clearFilters = function() {
        $location.search({});
        $scope.initValues();
        for (var j=0; j < $scope.projectStatus.tag_info.length ; j++) {
            $scope.projectStatus.tag_info[j].state = 0;
        }
        $scope.refreshFilters();
    };

    $scope.initValues();

    $scope.initFromQueryString = function() {
        var queryStringObject = $location.search();
        var shouldRefresh = false;
        if (angular.isDefined(queryStringObject.monitorFilter)) {
            shouldRefresh = true;
            $log.debug('projectStatusCtrl: Monitor filter init from query string - ' + queryStringObject.monitorFilter);
            $scope.selectedMonitor = queryStringObject.monitorFilter;
        }
        if (angular.isDefined(queryStringObject.tagsOn)) {
            shouldRefresh = true;
        }
        if (angular.isDefined(queryStringObject.tagsOff)) {
            shouldRefresh = true;
        }
        return shouldRefresh;
    };

    var tagsChangedHandler = $rootScope.$on(Constants.EVENTS.TAGS_CHANGED, function() {
        $scope.refreshFilters();
    });

    // Returns true if the node should be in the filtered array (Displayed)
    var isNodeFiltered = function(node) {
        return $filter('nodeFilter')(node,$scope.nodesFilter,$scope.selectedMonitor,$scope.projectStatus.tag_info);
    };

    var moveNodeToVisible = function(versionItem,node) {
        node.visible = true;
        if (!versionItem.visibleNodes) {
            versionItem.visibleNodes = [];
        }
        versionItem.visibleNodes.push(node);
    };

    for (var i1=0 ; i1 < $scope.projectStatus.nodes_for_version.length; i1++) {
        $scope.projectStatus.nodes_for_version[i1].filteredNodes = $scope.projectStatus.nodes_for_version[i1].nodes.slice();
        var maxNodesToShowHere = $scope.maxNodesToShow;
        if (maxNodesToShowHere > $scope.projectStatus.nodes_for_version[i1].filteredNodes.length || !$scope.projectStatus.more_nodes_enabled) {
            maxNodesToShowHere = $scope.projectStatus.nodes_for_version[i1].filteredNodes.length;
        }
        for (var j=0; j < maxNodesToShowHere; j++) {
            moveNodeToVisible($scope.projectStatus.nodes_for_version[i1],$scope.projectStatus.nodes_for_version[i1].filteredNodes[j]);
        }
    }

    for (var i2=0 ; i2 < $scope.projectStatus.nodes_for_version.length; i2++) {
        $scope.allNodesCount += $scope.projectStatus.nodes_for_version[i2].filteredNodes.length;
    }

    $scope.$watch("selectedMonitor",function( newName, oldName ) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('projectStatusCtrl: selectedMonitor was changed');
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

    $scope.showMore = function(count) {
        if ($scope.projectStatus.more_nodes_enabled) {
            return count < $scope.maxNodesToShow;
        }
        else {
            return true;
        }
    };
    $scope.refreshFilters = function() {
        var count = 0;
        for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
            $scope.projectStatus.nodes_for_version[i].filteredNodes.splice(0,$scope.projectStatus.nodes_for_version[i].filteredNodes.length);
            $scope.projectStatus.nodes_for_version[i].visibleNodes.splice(0,$scope.projectStatus.nodes_for_version[i].visibleNodes.length);
            for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].nodes.length; j++) {
                $scope.projectStatus.nodes_for_version[i].nodes[j].visible = false;
                if (isNodeFiltered($scope.projectStatus.nodes_for_version[i].nodes[j])) {
                    $scope.projectStatus.nodes_for_version[i].filteredNodes.push($scope.projectStatus.nodes_for_version[i].nodes[j]);
                    count++;
                }
            }
            if ($scope.projectStatus.nodes_for_version[i].filteredNodes.length > 0) {
                for (var k=0; $scope.showMore(k) && k < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; k++) {
                    moveNodeToVisible($scope.projectStatus.nodes_for_version[i],$scope.projectStatus.nodes_for_version[i].filteredNodes[k]);
                }
            }
        }
        $scope.allNodesCount = count;
    };

    if ($scope.initFromQueryString()) {
        $scope.refreshFilters();
    }

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

    $scope.selectedCount = function(){
        var count = 0;
        for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; j++) {
                if ($scope.projectStatus.nodes_for_version[i].filteredNodes[j].checked){
                    count++;
                }
            }
        }
        return count;
    };
    $scope.checkboxClick = function(versionItem, event) {
        event.stopPropagation();
        angular.forEach(versionItem.filteredNodes, function(item) {
           item.checked = event.target.checked && item.user_can_command;
        });
    };

    $scope.isAnyNodeChecked = function() {
        for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; j++) {
                if ($scope.projectStatus.nodes_for_version[i].filteredNodes[j].checked) {
                    return true;
                }
            }
        }
        return false;
    };

    $scope.getAllSelectedNodes = function() {
        var res = [];
        for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; j++) {
                if ($scope.projectStatus.nodes_for_version[i].filteredNodes[j].checked) {
                    res.push($scope.projectStatus.nodes_for_version[i].filteredNodes[j]);
                }
            }
        }
        return res;
    };

    $scope.isAllNodesChecked = function() {
        if ($scope.allNodesCount === 0) {
            return false;
        }
        for (var i=0 ; i < $scope.projectStatus.nodes_for_version.length; i++) {
            for (var j=0 ; j < $scope.projectStatus.nodes_for_version[i].filteredNodes.length; j++) {
                if (!$scope.projectStatus.nodes_for_version[i].filteredNodes[j].checked) {
                    return false;
                }
            }
        }
        return true;
    };

    $scope.isVersionChecked = function(versionItem) {
        for (var i=0 ; i < versionItem.filteredNodes.length; i++) {
            if (!versionItem.filteredNodes[i].checked) {
                return false;
            }

        }
        return true;
    };

    $scope.doSelectAllNodes = function(event) {
        event.stopPropagation();
        angular.forEach($scope.projectStatus.nodes_for_version, function(versionItem) {
            angular.forEach(versionItem.filteredNodes, function(node) {
                node.checked = event.target.checked && node.user_can_command;
            });
        });
    };

    $scope.isNodeDisabled = function(node){
        return !node.user_can_command;
    };
    var unRegisterFunction = $rootScope.$on(Constants.EVENTS.BREADCRUMB_CLICKED, function () {
        $scope.clearFilters();
    });

    $scope.$on('$destroy',unRegisterFunction);

    $scope.$on('$destroy', tagsChangedHandler);
}]);
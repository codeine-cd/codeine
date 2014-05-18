'use strict';
angular.module('codeine').controller('projectsListCtrl',['$scope', '$log', 'projects', 'tabs', '$filter', function($scope, $log, projects, tabs, $filter) {
    $scope.tabs = tabs.slice(0);
    $scope.tabs.unshift({name:"main", exp: [".*"]});
    $scope.projects = projects;

    $scope.shouldShowTab = function(){
        return function(tab){
            return $filter('projectsFilter')($scope.projects, tab).length > 0;
        }
    }
}]);
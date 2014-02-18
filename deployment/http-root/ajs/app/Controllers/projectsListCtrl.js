angular.module('codeine').controller('projectsListCtrl',['$scope', '$log', 'projects', 'tabs', function($scope, $log, projects, tabs) {
    $scope.tabs = tabs.slice(0);
    $scope.tabs.unshift({name:"main", exp: [".*"]});
    $scope.projects = projects;
}]);
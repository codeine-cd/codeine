angular.module('codeine').controller('projectsListCtrl',['$scope', '$log', 'projects', 'tabs', function($scope, $log, projects, tabs) {
    $scope.app.sideBarFile = "/ajs/partials/menus/main.html";
    $scope.tabs = tabs;
    $scope.projects = projects;
}]);
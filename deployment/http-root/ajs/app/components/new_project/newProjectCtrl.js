'use strict';
angular.module('codeine').controller('newProjectCtrl',['$scope', '$log', 'projects',function($scope, $log, projects) {
    $scope.projects = projects;
    $scope.new_project_data = {};
    $scope.new_project_data.type = "New";
}]);
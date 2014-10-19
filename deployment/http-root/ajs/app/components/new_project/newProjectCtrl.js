(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function newProjectCtrl($scope, projects) {
        $scope.projects = projects;
        $scope.new_project_data = {};
        $scope.new_project_data.type = "New";
    }

    //// Angular Code ////
    angular.module('codeine').controller('newProjectCtrl', newProjectCtrl);

})(angular);
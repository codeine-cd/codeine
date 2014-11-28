(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,projectConfiguration,projectStatus, projects) {
        $scope.projectStatus = projectStatus;
        $scope.projectConfiguration = projectConfiguration;
        $scope.projects = projects;
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,projectConfiguration,projectStatus, commands) {
        $scope.projectStatus = projectStatus;
        $scope.projectConfiguration = projectConfiguration;
        $scope.projects = commands;
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
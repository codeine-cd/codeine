(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,project) {
        $scope.projectStatus = project.status;
        $scope.projectConfiguration = project.configuration;
        $scope.commands = project.runnableCommands;
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
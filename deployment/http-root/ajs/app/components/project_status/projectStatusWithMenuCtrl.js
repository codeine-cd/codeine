(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,projectConfiguration,projectStatus) {
        $scope.projectStatus = projectStatus;
        $scope.projectConfiguration = projectConfiguration;
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
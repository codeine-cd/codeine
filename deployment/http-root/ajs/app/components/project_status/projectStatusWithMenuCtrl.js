(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,$log,projectConfiguration,projectStatus) {
        $log.debug('projectStatusWithMenuCtrl: created');
        $scope.projectStatus = projectStatus;
        $scope.projectConfiguration = projectConfiguration;
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
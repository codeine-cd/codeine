angular.module('codeine').controller('projectStatusWithMenuCtrl',['$scope','$log','projectConfiguration','projectStatus',
    function($scope,$log,projectConfiguration,projectStatus) {
        $log.debug('projectStatusWithMenuCtrl: created');
        $scope.projectStatus = projectStatus;
        $scope.projectConfiguration = projectConfiguration;
    }]);
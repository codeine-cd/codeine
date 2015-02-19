(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectStatusWithMenuCtrl($scope,project) {
        $scope.projectStatusImmutable = project.status;
        $scope.projectConfiguration = project.configuration;
        $scope.commands = project.runnableCommands;
        $scope.projectStatus = {};
        $scope.projectStatus.tag_info = [];
        for (var i12=0 ; i12 < $scope.projectStatusImmutable.tag_info.length; i12++) {
            $scope.projectStatus.tag_info.push({immutable:$scope.projectStatusImmutable.tag_info[i12]});
        }
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectStatusWithMenuCtrl',projectStatusWithMenuCtrl);

})(angular);
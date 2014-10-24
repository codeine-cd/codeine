(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectMenuCtrl($scope, $routeParams, $window, $location, LoginService, CodeineService,AlertService) {
        /*jshint validthis:true */
        var vm = this;

        vm.projectName = $routeParams.project_name;
        vm.permissions = LoginService.getSessionInfo().permissions;

        vm.deleteProject = function() {
            if ($window.confirm('Are you sure you would like to delete project ' + $scope.projectName + ' ? THIS IS NOT REVERSIBLE!!!')) {
                CodeineService.deleteProject($scope.projectName).success(function() {
                    AlertService.addAlert('success','Project ' + $scope.projectName + ' was deleted successfully',3000);
                    $location.path('/codeine');
                });
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectMenuCtrl', projectMenuCtrl);

})(angular);
angular.module('codeine').controller('projectMenuCtrl',['$scope', '$log', '$routeParams', '$window', '$location','CodeineService','AlertService',
    function($scope, $log,$routeParams, $window, $location,CodeineService,AlertService) {
        $scope.projectName = $routeParams.project_name;
        $log.debug('projectMenuCtrl: current project is ' + $scope.projectName);

        $scope.deleteProject = function() {
            if ($window.confirm('Are you sure you would like to delete project ' + $scope.projectName + ' ? THIS IS NOT REVERSIBLE!!!')) {
                $log.debug('projectMenuCtrl: will delete project');
                CodeineService.deleteProject($scope.projectName).success(function() {
                    AlertService.addAlert('success','Project ' + $scope.projectName + ' was deleted successfully',3000);
                    $location.path('/codeine');
                });
            }
        };
    }]);
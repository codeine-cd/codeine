angular.module('codeine').controller('commandRunningCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('commandRunningCtrl: created for project ' + $scope.projectName);
    $scope.limit = 1;
    var getHistory = function() {
        CodeineService.getRunningCommands().success(function(data) {
            $scope.history = data;
        });
    }

    getHistory();
    $interval(getHistory,5000,0,true);

}]);
angular.module('codeine').controller('commandRunningCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('commandRunningCtrl: created for project ' + $scope.projectName);
    $scope.limit = 10;
    var getHistory = function() {
        CodeineService.getRunningCommands().success(function(data) {
            if (!$scope.history || $scope.history.length !== data.length) {
                $scope.history = data;
            }
        });
    }

    getHistory();
    $interval(getHistory,5000,0,true);

}]);
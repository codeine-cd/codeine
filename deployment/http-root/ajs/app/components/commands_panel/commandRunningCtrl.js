'use strict';
angular.module('codeine').controller('commandRunningCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService) {
    $scope.projectName = $routeParams.project_name;
    $scope.limit = 10;

    CodeineService.getRunningCommands().success(function(data) {
        $scope.history = data;
    });

    var interval = setInterval(function() {
        $.ajax( {
            type: 'GET',
            url: '/commands-status_json',
            success: function(response) {
                if  (($scope.history.length !== response.length) || (angular.toJson($scope.history) !== angular.toJson(response))) {
                    $scope.$apply(function() {
                        $scope.history = response;
                    });
                }
            },
            error:  function(err) {
                $log.error('commandRunningCtrl: ' + err);
            },
            dataType: 'json'
        });
    },5000);

    $scope.$on('$destroy', function() {
        clearInterval(interval);
    });
}]);
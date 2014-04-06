'use strict';
angular.module('codeine').controller('commandHistoryCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService) {
    $scope.projectName = $routeParams.project_name;
    $scope.limit = 10;

    CodeineService.getProjectCommandHistory($scope.projectName).success(function(data) {
        $scope.history = data;
    });

    var interval = setInterval(function() {
        if (!$scope.app.isInFocus) {
            return;
        }
        $.ajax( {
            type: 'GET',
            url: '/api/commands-log?project=' + $scope.projectName  ,
            success: function(response) {
                if  (($scope.history.length !== response.length) || (angular.toJson($scope.history) !== angular.toJson(response))) {
                    $scope.$apply(function() {
                        $scope.history = response;
                    });
                }
            },
            error:  function(err) {
                $log.error('commandHistoryCtrl: ' + err);
            },
            dataType: 'json'
        });
    },5000);

    $scope.$on('$destroy', function() {
        clearInterval(interval);
    });
}]);

'use strict';
angular.module('codeine').controller('commandRunningCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService','Constants', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService,Constants) {
    $scope.projectName = $routeParams.project_name;
    $scope.limit = 10;

    CodeineService.getRunningCommands().success(function(data) {
        $scope.history = data;
    });
    var maxUpdatesNotInFocus = 100;
    var intervalTriggered = 0;
    var interval = setInterval(function() {
        if (!$scope.app.isInFocus && intervalTriggered > maxUpdatesNotInFocus) {
            return;
        }
        $.ajax( {
            type: 'GET',
            url: Constants.CODEINE_WEB_SERVER + '/api/commands-status',
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
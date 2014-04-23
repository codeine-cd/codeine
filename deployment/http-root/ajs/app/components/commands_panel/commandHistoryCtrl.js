'use strict';
angular.module('codeine').controller('commandHistoryCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService','Constants', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService,Constants) {
    $scope.projectName = $routeParams.project_name;
    $scope.limit = 10;

    CodeineService.getProjectCommandHistory($scope.projectName).success(function(data) {
        $scope.history = data;
    });
    var maxUpdatesNotInFocus = 100;
    var intervalTriggered = 0;
    var interval = setInterval(function() {
        $log.debug('checking commandHistoryCtrl ' + $scope.app.isInFocus + ' ' + intervalTriggered);
        if (!$scope.app.isInFocus && intervalTriggered < maxUpdatesNotInFocus) {
            $log.debug('commandHistoryCtrl will not update');
            return;
        }
        intervalTriggered++;
        $.ajax( {
            type: 'GET',
            url: Constants.CODEINE_WEB_SERVER + '/api/commands-log?project=' + $scope.projectName  ,
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

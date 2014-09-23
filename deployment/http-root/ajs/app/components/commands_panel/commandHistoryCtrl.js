'use strict';
angular.module('codeine').controller('commandHistoryCtrl',['$scope', '$rootScope', '$log', '$interval','$routeParams','CodeineService','Constants', '$route', function($scope,$rootScope,$log,$interval,$routeParams,CodeineService,Constants,$route) {
    $scope.projectName = $route.current.params.project_name;
    $scope.limit = 10;
    $scope.historyUrl = Constants.CODEINE_WEB_SERVER + '/api/commands-log?project=' + encodeURI($scope.projectName);
    $log.debug('node name is ' + $route.current.params.node_name);
    if ($route.current.params.node_name !== undefined) {
        $scope.historyUrl += '&node=' + encodeURI($route.current.params.node_name);
    }
    CodeineService.getProjectCommandHistory($scope.projectName, $route.current.params.node_name).success(function(data) {
        $scope.history = data;
    });
    var maxUpdatesNotInFocus = 100;
    var intervalTriggered = 0;
    var interval = setInterval(function() {
        //$log.debug('checking commandHistoryCtrl ' + $scope.app.isInFocus + ' ' + intervalTriggered);
        if (!$scope.app.isInFocus && intervalTriggered > maxUpdatesNotInFocus) {
            //$log.debug('commandHistoryCtrl will not update');
            return;
        }
        intervalTriggered++;
        $.ajax( {
            type: 'GET',
            url: $scope.historyUrl ,
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

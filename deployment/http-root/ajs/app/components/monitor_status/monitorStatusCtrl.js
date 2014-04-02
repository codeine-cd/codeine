'use strict';
angular.module('codeine').controller('monitorStatusCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'monitorStatus',
    function($scope, $log,$routeParams, CodeineService, monitorStatus ) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('monitorStatusCtrl: current project is ' + $scope.projectName);
    $scope.monitorStatus = monitorStatus;

}]);
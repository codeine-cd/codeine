angular.module('codeine').controller('manageMenuCtrl',['$scope', '$rootScope', '$log', 'CodeineService', 'AlertService', function($scope,$rootScope,$log,CodeineService,AlertService) {
    $log.debug('manageMenuCtrl: created');
    $scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
    $scope.prepareForShutdown = function() {
        $log.debug('manageMenuCtrl: prepareForShutdown');
        CodeineService.prepareForShutdown().success(function(data) {
            $log.debug('prepareForShutdown - success');
            $log.debug('run: got session info ' + angular.toJson(data));
            $rootScope.app.sessionInfo = data;
            $scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
        });
    };
    $scope.cancelShutdown = function() {
    	$log.debug('manageMenuCtrl: cancelShutdown');
    	CodeineService.cancelShutdown().success(function(data) {
    		$log.debug('cancelShutdown - success');
    		$log.debug('run: got session info ' + angular.toJson(data));
    		$rootScope.app.sessionInfo = data;
    		$scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
    	});
    };
}]);
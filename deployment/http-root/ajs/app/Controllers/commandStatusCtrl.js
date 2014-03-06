angular.module('codeine').controller('commandStatusCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'commandStatus',
    function($scope, $log,$routeParams, CodeineService, commandStatus ) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('monitorStatusCtrl: current project is ' + $scope.projectName);
    $scope.commandStatus = commandStatus;

}]);
angular.module('codeine').controller('nodeStatusCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'nodeStatus',
    function($scope, $log,$routeParams, CodeineService, nodeStatus ) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('nodeStatusCtrl: current project is ' + $scope.projectName);
    $scope.nodeStatus = nodeStatus;

}]);
angular.module('codeine').controller('projectConfigureCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'projectConfigurationForEditing',
    function($scope, $log,$routeParams, CodeineService, projectConfigurationForEditing ) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('projectConfigureCtrl: current project is ' + $scope.projectName);
    $scope.projectConfigurationForEditing = projectConfigurationForEditing;
}]);
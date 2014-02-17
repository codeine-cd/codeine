angular.module('codeine').controller('projectConfigureCtrl',['$scope', '$log', '$routeParams', 'CodeineService', function($scope, $log,$routeParams, CodeineService ) {
    $scope.app.sideBarFile = "/ajs/partials/menus/projectMenu.html";
    $scope.projectName = $routeParams.project_name;
    $log.debug('projectConfigureCtrl: current project is ' + $scope.projectName);
    $scope.projectConfigurationForEditing = CodeineService.getProjectConfiguration($scope.projectName);
}]);
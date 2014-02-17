angular.module('codeine').controller('projectStatusCtrl',['$scope', '$log', '$routeParams', function($scope, $log,$routeParams ) {
    $scope.app.sideBarFile = "/ajs/partials/menus/projectMenu.html";
    $scope.projectName = $routeParams.project_name;
    $log.debug('projectStatusCtrl: current project is ' + $scope.projectName);
}]);
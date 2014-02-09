angular.module('codeine').controller('manageCodeineCtrl',['$scope', '$log', 'tabs','permissions', function($scope, $log, tabs, permissions) {
    $scope.app.sideBarFile = "/ajs/partials/menus/manage.html";
    $scope.tabs = tabs;
    $scope.permissions = permissions;
    $scope.currentConfiguration = angular.copy($scope.app.globalConfiguration);

}]);



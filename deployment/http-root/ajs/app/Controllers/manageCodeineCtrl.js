angular.module('codeine').controller('manageCodeineCtrl',['$scope', '$log', 'tabs','permissions', function($scope, $log, tabs, permissions) {
    $scope.app.sideBarFile = "/ajs/partials/menus/manage.html";
    $scope.tabsForEditing = angular.copy(tabs);
    $scope.permissionsForEditing = angular.copy(permissions);
    $scope.globalConfigurationForEditing = angular.copy($scope.app.globalConfiguration);

}]);



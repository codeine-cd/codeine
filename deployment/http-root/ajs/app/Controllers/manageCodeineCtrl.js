angular.module('codeine').controller('manageCodeineCtrl',['$scope', '$log', 'tabs','permissions', function($scope, $log, tabs, permissions) {
    $scope.app.sideBarFile = "/ajs/partials/menus/manage.html";
    $scope.tabsForEditing = angular.copy(tabs);
    $scope.permissionsForEditing = angular.copy(permissions);
    $scope.globalConfigurationForEditing = angular.copy($scope.app.globalConfiguration);
//    $scope.new_username = "";
    $scope.addUser = function() {
        $scope.permissionsForEditing.push({username : $scope.new_username});
//        $scope.new_username = "";
    };
    $scope.addTab = function() {
    	$scope.tabsForEditing.push({"a":["a"]});
    };
    $scope.select2Options = {
//    		createSearchChoice: function(term) {
//    			return {id: term, text: term};
//    		},
    		multiple: true
    };
}]);



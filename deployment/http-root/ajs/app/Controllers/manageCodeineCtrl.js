angular.module('codeine').controller('manageCodeineCtrl',['$scope', '$log', 'tabs','permissions', 'projects', function($scope, $log, tabs, permissions, projects) {
    $scope.projects  = [];
    angular.forEach(projects, function(key,value) {
        $scope.projects.push(key['name']);
    });
    $scope.app.sideBarFile = "/ajs/partials/menus/manage.html";
    $scope.tabsForEditing = angular.copy(tabs);
    $scope.permissionsForEditing = angular.copy(permissions);
    $scope.globalConfigurationForEditing = angular.copy($scope.app.globalConfiguration);

    $scope.saveConfiguration = function() {
        $log.debug('manageCodeineCtrl: saveConfiguration');
    }

    $scope.saveTabs = function() {
        $log.debug('manageCodeineCtrl: saveTabs');
    }

    $scope.addUser = function() {
        $scope.permissionsForEditing.push({});
    };

    $scope.removeUser = function(index) {
        $scope.permissionsForEditing.splice(index,1);
    };

    $scope.addTab = function() {
    	$scope.tabsForEditing.push({});
    };

    $scope.removeTab = function(index) {
        $scope.tabsForEditing.splice(index,1);
    };

    $scope.select2Options = {
        'multiple': true,
        'simple_tags': true,
        'tags': $scope.projects ,
        'tokenSeparators': [",", " "]
    };
}]);



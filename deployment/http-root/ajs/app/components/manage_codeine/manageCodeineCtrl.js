(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageCodeineCtrl($scope, $log, tabs, permissions, projects, CodeineService, AlertService, LoginService) {
        $scope.projects  = [];
        angular.forEach(projects, function(key) {
            $scope.projects.push(key.name);
        });
        $scope.tabsForEditing = angular.copy(tabs);
        $scope.permissionsForEditing = angular.copy(permissions);
        $scope.globalConfigurationForEditing = angular.copy($scope.app.globalConfiguration);

        $scope.addMysql = function() {
            $scope.globalConfigurationForEditing.mysql.push({});
        };

        $scope.removeMysql = function(index) {
            $scope.globalConfigurationForEditing.mysql.splice(index,1);
        };

        $scope.setViewAs = function() {
            LoginService.setViewAs($scope.newViewAs);
        };

        $scope.saveConfiguration = function() {
            $log.debug('manageCodeineCtrl: saveConfiguration');
            CodeineService.updateGlobalConfiguration($scope.globalConfigurationForEditing).success(function(data) {
                $log.debug('manageCodeineCtrl: update configuration -' + angular.toJson(data));
                AlertService.addAlert('success','Configuration was saved successfully');
                $scope.app.globalConfiguration = data;
            });
        };

        $scope.saveTabs = function() {
            $log.debug('manageCodeineCtrl: saveTabs');
            CodeineService.updateViewTabs($scope.tabsForEditing).success(function(data) {
                $log.debug('manageCodeineCtrl: update tabs -' + angular.toJson(data));
                AlertService.addAlert('success','Tabs were saved successfully');
            });

        };

        $scope.savePermissions = function() {
            $log.debug('manageCodeineCtrl: savePermissions');
            CodeineService.updatePermissions($scope.permissionsForEditing).success(function(data) {
                $log.debug('manageCodeineCtrl: update permissions -' + angular.toJson(data));
                AlertService.addAlert('success','Permissions were saved successfully');
            });

        };

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
    }


    //// Angular Code ////
    angular.module('codeine').controller('manageCodeineCtrl',manageCodeineCtrl);

})(angular);
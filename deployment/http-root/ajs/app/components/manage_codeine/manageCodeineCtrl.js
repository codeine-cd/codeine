(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageCodeineCtrl(tabs, permissions, projects, CodeineService, AlertService, LoginService, CodeineConfigurationService) {
        /*jshint validthis:true */
        var vm = this;

        vm.projects  = [];

        angular.forEach(projects, function(key) {
            vm.projects.push(key.name);
        });

        vm.tabsForEditing = angular.copy(tabs);
        vm.permissionsForEditing = angular.copy(permissions);
        vm.globalConfigurationForEditing = angular.copy(CodeineConfigurationService.getGlobalConfiguration());

        vm.addMysql = function() {
            vm.globalConfigurationForEditing.mysql.push({});
        };

        vm.removeMysql = function(index) {
            vm.globalConfigurationForEditing.mysql.splice(index,1);
        };

        vm.setViewAs = function() {
            LoginService.setViewAs(vm.newViewAs);
        };

        vm.saveConfiguration = function() {
            CodeineConfigurationService.updateGlobalConfiguration(vm.globalConfigurationForEditing).then(function() {
                AlertService.addAlert('success','Configuration was saved successfully');
            }, function() {
                AlertService.addAlert('danger','Failed to save configuration');
            });
        };

        vm.saveTabs = function() {
            CodeineService.updateViewTabs(vm.tabsForEditing).success(function() {
                AlertService.addAlert('success','Tabs were saved successfully');
            });

        };

        vm.savePermissions = function() {
            CodeineService.updatePermissions(vm.permissionsForEditing).success(function() {
                AlertService.addAlert('success','Permissions were saved successfully');
            });

        };

        vm.addUser = function() {
            vm.permissionsForEditing.push({});
        };

        vm.removeUser = function(index) {
            vm.permissionsForEditing.splice(index,1);
        };

        vm.addTab = function() {
            vm.tabsForEditing.push({});
        };

        vm.removeTab = function(index) {
            vm.tabsForEditing.splice(index,1);
        };

        vm.select2Options = {
            'multiple': true,
            'simple_tags': true,
            'tags': vm.projects ,
            'tokenSeparators': [",", " "]
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('manageCodeineCtrl',manageCodeineCtrl);

})(angular);
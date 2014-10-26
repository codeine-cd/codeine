(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageCodeineCtrl(tabs, permissions, projects, CodeineService, AlertService, LoginService, CodeineConfigurationService) {
        /*jshint validthis:true */
        var vm = this;

        function addSuccessMessage(msg) {
            AlertService.addAlert('success',msg);
        }

        function addFailMessage(msg) {
            AlertService.addAlert('danger',msg);
        }

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
                addSuccessMessage('success','Configuration was saved successfully');
            }, function() {
                addFailMessage('danger','Failed to save Configuration');
            });
        };

        vm.saveTabs = function() {
            CodeineService.updateViewTabs(vm.tabsForEditing).success(function() {
                addSuccessMessage('success','Tabs were saved successfully');
            }, function() {
                addFailMessage('danger','Failed to save Tabs');
            });

        };

        vm.savePermissions = function() {
            CodeineService.updatePermissions(vm.permissionsForEditing).success(function() {
                addSuccessMessage('success','Permissions were saved successfully');
            }, function() {
                addFailMessage('danger','Failed to save Permissions');
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
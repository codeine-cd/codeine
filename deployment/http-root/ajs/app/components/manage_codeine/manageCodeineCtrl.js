(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageCodeineCtrl(tabs, permissions, projects, CodeineService, AlertService, LoginService, CodeineConfigurationService, $log) {
        /*jshint validthis:true */
        var vm = this;
        vm.admin_is_open = true;

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

        for (var i1=0; i1 < vm.tabsForEditing.length ; i1++) {
            if (vm.tabsForEditing[i1].exp.length === 0) {
                delete vm.tabsForEditing[i1].exp;
            }
        }

        for (var i2=0; i2 < vm.permissionsForEditing.length ; i2++) {
            if (vm.permissionsForEditing[i2].read_project.length === 0) {
                delete vm.permissionsForEditing[i2].read_project;
            }
            if (vm.permissionsForEditing[i2].command_project.length === 0) {
                delete vm.permissionsForEditing[i2].command_project;
            }
            if (vm.permissionsForEditing[i2].configure_project.length === 0) {
                delete vm.permissionsForEditing[i2].configure_project;
            }
        }

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
            vm.codeineConfigPromise = CodeineConfigurationService.updateGlobalConfiguration(vm.globalConfigurationForEditing).then(function() {
                addSuccessMessage('Configuration was saved successfully');
            }, function() {
                addFailMessage('Failed to save Configuration');
            });
        };

        vm.saveTabs = function() {
            vm.codeineTabsPromise = CodeineService.updateViewTabs(vm.tabsForEditing).success(function() {
                addSuccessMessage('Tabs were saved successfully');
            }, function() {
                addFailMessage('Failed to save Tabs');
            });

        };

        vm.savePermissions = function() {
            vm.codeinePermissionsPromise = CodeineService.updatePermissions(vm.permissionsForEditing).success(function() {
                addSuccessMessage('Permissions were saved successfully');
            }, function() {
                addFailMessage('Failed to save Permissions');
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

        vm.pushProjectsToDb = function() {
            $log.debug('reloadProjects');
            CodeineService.pushProjectsToDb().success(function() {
                AlertService.addAlert('success','Project Configuration was pushed to databases',3000);
            });
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('manageCodeineCtrl',manageCodeineCtrl);

})(angular);
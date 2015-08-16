(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectsListCtrl(projects, tabs, $filter, $routeParams, $log, $location) {
        /*jshint validthis:true */
        var vm = this;

        vm.selectedTabName = $routeParams.tab_name;
        vm.uiTabs = [];
        vm.uiTabs.push({tab:{name:'main', exp: ['.*']}, active : vm.selectedTabName === 'main'});
        _.each(tabs, function(tab) {
            vm.uiTabs.push( { tab : tab, active : tab.name === vm.selectedTabName});
        });
        vm.projects = projects;

        vm.shouldShowTab = function(){
            return function(tab){
                return $filter('projectsFilter')(vm.projects, tab).length > 0;
            };
        };

        vm.changeTab = function(tab) {
            $location.path('/codeine/view/' + tab.name);
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectsListCtrl', projectsListCtrl);

})(angular);


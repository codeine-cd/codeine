(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectsListCtrl(projects, tabs, $filter) {
        /*jshint validthis:true */
        var vm = this;
        vm.tabs = tabs.slice(0);
        vm.tabs.unshift({name:"main", exp: [".*"]});
        vm.projects = projects;

        vm.shouldShowTab = function(){
            return function(tab){
                return $filter('projectsFilter')(vm.projects, tab).length > 0;
            };
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectsListCtrl', projectsListCtrl);

})(angular);


(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectsListCtrl($scope, projects, tabs, $filter) {
        $scope.tabs = tabs.slice(0);
        $scope.tabs.unshift({name:"main", exp: [".*"]});
        $scope.projects = projects;

        $scope.shouldShowTab = function(){
            return function(tab){
                return $filter('projectsFilter')($scope.projects, tab).length > 0;
            };
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectsListCtrl', projectsListCtrl);

})(angular);


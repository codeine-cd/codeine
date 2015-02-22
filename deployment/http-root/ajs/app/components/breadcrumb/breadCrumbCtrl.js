(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function breadCrumbCtrl($rootScope,$scope,$log,$location,Constants) {
        /*jshint validthis:true */
        var vm = this;

        vm.gotoUrl = function(url) {
            $location.url(url);
            $rootScope.$emit(Constants.EVENTS.BREADCRUMB_CLICKED,url);
        };

        var handler = $rootScope.$on("$routeChangeSuccess", function (event, current) {
            vm.items = [];
            vm.lastItem = '';

            function capitaliseFirstLetter(string) {
                return string.charAt(0).toUpperCase() + string.slice(1);
            }

            var path = $location.path().split('/');

            if ($location.path() === '/codeine/nodes/status') {
                vm.items.push( { name : 'Manage Codeine', url : '/codeine/manage-codeine'});
                vm.lastItem = 'Codeine Nodes Status';
                return;
            }

            if (path[path.length -1] === 'new_project') {
                vm.lastItem = 'New Project';
                return;
            }
            if (path[path.length -1] === 'manage-codeine') {
                vm.items.push( { name : 'Manage Codeine', url : $location.path()});
                vm.lastItem = 'Configure Codeine';
                return;
            }
            if ($location.path() === '/codeine/manage/statistics') {
                vm.items.push( { name : 'Manage Codeine', url : '/codeine/manage-codeine'});
                vm.lastItem = 'Codeine Statistics';
                return;
            }

            if (angular.isDefined(current.params.user_name)) {
                vm.lastItem = current.params.user_name;
                return;
            }
            var view = "main";
            if (angular.isDefined(current.params.tab_name)) {
                view = current.params.tab_name;
                vm.items.push( { name : current.params.tab_name, url : '/codeine/view/' + view });
            }
            if (angular.isDefined(current.params.project_name)) {
                vm.items.push( { name : current.params.project_name, url : '/codeine/view/' + view + '/project/' + current.params.project_name + '/status' });
            }
            if (angular.isDefined(current.params.node_name)) {
                if (angular.isDefined(current.params.collector_name)) {
                    vm.items.push( { name : current.params.node_name, url : '/codeine/view/' + view + '/project/' + current.params.project_name + '/node/' +  current.params.node_name + '/status' });
                    vm.lastItem = current.params.collector_name;
                    return;
                }
                vm.lastItem = current.params.node_name;
                return;
            }
            if (angular.isDefined(current.params.command_id)) {
                vm.lastItem = current.params.command_name;
                return;
            }

            vm.lastItem = capitaliseFirstLetter(path[path.length-1]);

        });

        $scope.$on('$destroy', function() {
            handler();
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('breadCrumbCtrl',breadCrumbCtrl);

})(angular);
'use strict';

angular.module('codeine').controller('breadCrumbCtrl',['$scope','$rootScope', '$log','$location', function($scope,$rootScope,$log,$location) {

    $rootScope.$on("$routeChangeSuccess", function (event, current) {
        $log.debug('breadCrumbCtrl: $routeChangeSuccess');
        $log.debug('breadCrumbCtrl: current =' + angular.toJson(current));
        $log.debug('breadCrumbCtrl: $location.path() = ' + $location.path());
        $scope.items = [];
        $scope.lastItem = '';

        function capitaliseFirstLetter(string) {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }

        var path = $location.path().split('/');

        if ($location.path() === '/codeine/nodes/status') {
            $scope.items.push( { name : 'Manage Codeine', url : '/codeine/manage-codeine'});
            $scope.lastItem = 'Codeine Nodes Status';
            return;
        }

        if (path[path.length -1] === 'new_project') {
            $scope.lastItem = 'New Project';
            return;
        }
        if (path[path.length -1] === 'manage-codeine') {
            $scope.items.push( { name : 'Manage Codeine', url : $location.path()});
            $scope.lastItem = 'Configure Codeine';
            return;
        }

        if (angular.isDefined(current.params.user_name)) {
            $scope.lastItem = current.params.user_name;
            return;
        }
        if (angular.isDefined(current.params.project_name)) {
            $scope.items.push( { name : current.params.project_name, url : '/codeine/project/' + current.params.project_name + '/status' });
        }
        if (angular.isDefined(current.params.node_name)) {
            if (angular.isDefined(current.params.monitor_name)) {
                $scope.items.push( { name : current.params.node_name, url : '/codeine/project/' + current.params.project_name + '/node/' +  current.params.node_name + '/status' });
                $scope.lastItem = current.params.monitor_name;
                return;
            }
            $scope.lastItem = current.params.node_name;
            return;
        }
        if (angular.isDefined(current.params.command_id)) {
            $scope.lastItem = current.params.command_name;
            return;
        }

        $scope.lastItem = capitaliseFirstLetter(path[path.length-1]);

    });
}]);

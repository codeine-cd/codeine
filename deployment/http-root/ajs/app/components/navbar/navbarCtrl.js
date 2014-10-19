(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function navbarCtrl($scope,$log,CodeineService,$location) {
        $log.debug('navbarCtrl: created');

        $scope.model  = {
            projectSearch : ''
        };

        CodeineService.getProjects().success(function(data) {
            $log.debug('navbarCtrl: got ' + data.length + ' projects from server');
            $scope.projects = data;
        });

        var watchHandler = $scope.$watch('model.projectSearch', function(value) {
            if (value) {
                $log.debug('navbarCtrl: selected project: ' + value);
                $location.path('/codeine/project/' + value + '/status').replace();
            }
            $scope.model.projectSearch = '';
        });

        $scope.$on('$destroy', function() {
            watchHandler();
        });

    }

    //// Angular Code ////
    angular.module('codeine').controller('navbarCtrl', navbarCtrl);

})(angular);
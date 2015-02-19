(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function navbarCtrl($scope,$log,ProjectsRepository,$location,LoginService,CodeineConfigurationService) {

        $scope.model  = {
            projectSearch : ''
        };

        $scope.globalConfiguration = CodeineConfigurationService.getGlobalConfiguration();
        $scope.experimentalConfiguration = CodeineConfigurationService.getExperimentalConfiguration();

        LoginService.gettingSessionInfo().then(function(data) {
            $scope.sessionInfo = data;
        });

        ProjectsRepository.getProjects().then(function(data) {
            $scope.projects = data;
        });

        var watchHandler = $scope.$watch('model.projectSearch', function(value) {
            if (value) {
                $log.debug('navbarCtrl: selected project: ' + value);
                $location.path('/codeine/project/' + value + '/status');
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
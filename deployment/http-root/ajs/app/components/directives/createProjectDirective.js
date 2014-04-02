'use strict';
angular.module('codeine').directive('createProject', ['$log','$rootScope','CodeineService','AlertService','$location', function ($log,$rootScope,CodeineService,AlertService,$location) {
    return {
        restrict: 'A',
        scope: {
            project : '='
        },
        link: function ($scope, element) {
            $scope.create= function() {
                $log.debug('will create project with:' + angular.toJson($scope.project));
                CodeineService.createProject($scope.project).success(function() {
                    $log.debug('created project');
                    AlertService.addAlert('success','Successfully created new project',3000);
                    CodeineService.getSessionInfo().success(function(data) {
                        $log.debug('createProject: refreshed session info');
                        $rootScope.app.sessionInfo = data;
                        $location.path('/codeine/project/' + $scope.project.project_name + '/configure');
                    });
                });
            };
            element.bind('click', $scope.create);
        }
    };
}]);
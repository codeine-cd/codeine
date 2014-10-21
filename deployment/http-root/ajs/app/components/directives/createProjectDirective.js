(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function createProject($log,CodeineService,LoginService,AlertService,$location) {
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
                        LoginService.gettingSessionInfo().then(function() {
                            $location.path('/codeine/project/' + $scope.project.project_name + '/configure');
                        });
                    });
                };
                element.bind('click', $scope.create);
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('createProject', createProject);

})(angular);
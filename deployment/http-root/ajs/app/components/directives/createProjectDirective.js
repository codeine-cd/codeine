(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function createProject($log,ProjectsRepository,AlertService,$location) {
        return {
            restrict: 'A',
            scope: {
                project : '='
            },
            link: function ($scope, element) {
                $scope.create= function() {
                    ProjectsRepository.addProject($scope.project).then(function() {
                        AlertService.addAlert('success','Successfully created new project',3000);
                        $location.path('/codeine/project/' + $scope.project.project_name + '/configure');
                    }) ;
                };
                element.bind('click', $scope.create);
            }
        };
    }

    //// Angular Code ////
    angular.module('codeine').directive('createProject', createProject);

})(angular);
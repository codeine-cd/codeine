angular.module('codeine').directive('createProject', ['$log','CodeineService','AlertService', function ($log, CodeineService,AlertService) {
    return {
        restrict: 'A',
        scope: {
            project : "="
        },
        link: function ($scope, element, attrs) {
            $scope.create= function() {
                $log.debug('will create prkect with:' + angular.toJson($scope.project));
                CodeineService.createProject($scope.project).success(function(data) {
                    $log.debug('created project');
                    AlertService.addAlert('success','Successfully created new project',3000);
                })
            };

            element.bind("click", $scope.create);
        }

    };

}]);
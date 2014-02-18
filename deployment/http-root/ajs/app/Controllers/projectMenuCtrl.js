angular.module('codeine').controller('projectMenuCtrl',['$scope', '$log', '$route',
    function($scope, $log,$route ) {
        $scope.projectName = $route.current.params.project_name;
    }]);

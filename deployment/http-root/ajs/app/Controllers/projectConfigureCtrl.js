angular.module('codeine').controller('projectConfigureCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'projectConfigurationForEditing',
    function($scope, $log,$routeParams, CodeineService, projectConfigurationForEditing ) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('projectConfigureCtrl: current project is ' + $scope.projectName);
    $scope.projectConfigurationForEditing = projectConfigurationForEditing;

    $scope.addNode = function() {
        $scope.projectConfigurationForEditing.nodes_info.push({});
    };

    $scope.removeNode = function(index) {
        $scope.projectConfigurationForEditing.nodes_info.splice(index,1);
    };

    $scope.select2Options = {
        'multiple': true,
        'simple_tags': true,
        //'tags': $scope.projects ,
        'tokenSeparators': [",", " "]
    };
}]);
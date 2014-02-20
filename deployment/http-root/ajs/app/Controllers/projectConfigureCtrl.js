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
    $scope.addNotification = function() {
        $scope.projectConfigurationForEditing.mail.push({});
    };
    $scope.removeNotification = function(index) {
        $scope.projectConfigurationForEditing.mail.splice(index,1);
    };
    $scope.applyConfiguration = function() {
        $log.debug('applyConfiguration: ' + angular.toJson($scope.projectConfigurationForEditing));
    };

    $scope.select2Options = {
        'multiple': true,
        'simple_tags': true,
        //'tags': $scope.projects ,
        'tokenSeparators': [",", " "]
    };
}]);
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
    $scope.addMonitor = function() {
        $scope.projectConfigurationForEditing.monitors.push({name: "new_monitor"});
    };
    $scope.removeMonitor = function(index) {
        $scope.projectConfigurationForEditing.monitors.splice(index,1);
    };
    $scope.addCommand = function() {
        $scope.projectConfigurationForEditing.commands.push({name: "new_command", parameters: []});
    };
    $scope.removeCommand = function(index) {
        $scope.projectConfigurationForEditing.commands.splice(index,1);
    };
    $scope.addParameter = function(index) {
        $scope.projectConfigurationForEditing.commands[index].parameters.push({name: "NEW_PARAMETER"});
    };
    $scope.removeParameter = function(commandIndex, index) {
        $scope.projectConfigurationForEditing.commands[commandIndex].parameters.splice(index,1);
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
angular.module('codeine').controller('projectConfigureCtrl',['$scope', '$log', '$routeParams', 'CodeineService', 'projectConfigurationForEditing','$location','AlertService',
    function($scope, $log,$routeParams, CodeineService, projectConfigurationForEditing,$location,AlertService) {
    $scope.projectName = $routeParams.project_name;
    $log.debug('projectConfigureCtrl: current project is ' + $scope.projectName);
    $scope.projectConfigurationForEditing = projectConfigurationForEditing;
    $scope.tags = [];
    $scope.model = {
        commandAndMonitorAutoComplete : ['CODEINE_PROJECT_NAME','CODEINE_NODE_NAME','CODEINE_NODE_ALIAS','CODEINE_NODE_TAGS'],
        versionAutoComplete : ['CODEINE_PROJECT_NAME','CODEINE_NODE_NAME','CODEINE_NODE_ALIAS','CODEINE_NODE_TAGS','CODEINE_OUTPUT_FILE'],
        nodesDiscoveryAutoComplete : ['CODEINE_PROJECT_NAME','CODEINE_OUTPUT_FILE'],
        tagsAutoComplete : ['CODEINE_PROJECT_NAME','CODEINE_NODE_NAME','CODEINE_NODE_ALIAS','CODEINE_OUTPUT_FILE']
    };

    function swapItems(index,array) {
        var temp = array[index];
        array[index] = array[index+1];
        array[index+1] = temp;
    };

    for (var i=0; i < projectConfigurationForEditing.nodes_info.length ; i++) {
        for (var j=0; j < projectConfigurationForEditing.nodes_info[i].tags.length ; j++) {
            if ($scope.tags.indexOf(projectConfigurationForEditing.nodes_info[i].tags[j]) === -1) {
                $scope.tags.push(projectConfigurationForEditing.nodes_info[i].tags[j]);
            }
        }
    }

    $scope.addNode = function() {
        $scope.projectConfigurationForEditing.nodes_info.push({});
    };

    $scope.addMonitor = function() {
        $scope.projectConfigurationForEditing.monitors.push({name: "new_monitor_" + $scope.projectConfigurationForEditing.monitors.length});
    };

    $scope.removeItem = function(array,index) {
        array.splice(index,1);
    };

    $scope.moveUp = function(array,index,$event) {
        $event.preventDefault();
        $event.stopPropagation();
        swapItems(index-1,array);
    };

    $scope.moveDown = function(array,index,$event) {
        $event.preventDefault();
        $event.stopPropagation();
        swapItems(index,array);
    };

    $scope.addCommand = function() {
        $scope.projectConfigurationForEditing.commands.push({name: "new_command_" + $scope.projectConfigurationForEditing.commands.length, parameters: [], concurrency : 1, command_strategy : 'Immediately', duration_units : 'Minutes', ratio : 'Linear'});
    };

    $scope.addParameter = function(index) {
        $scope.projectConfigurationForEditing.commands[index].parameters.push({name: "NEW_PARAMETER", type : 'String'});
    };

    $scope.addNotification = function() {
        $scope.projectConfigurationForEditing.mail.push(  {intensity : 'Immediately'});
    };

    $scope.applyConfiguration = function(redirect) {
        $log.debug('applyConfiguration: ' + angular.toJson($scope.projectConfigurationForEditing));
        CodeineService.saveProjectConfiguration($scope.projectConfigurationForEditing).success(function() {
            AlertService.addAlert('success','Project Configuration was saved successfully',3000);
            if (redirect) {
                $location.path('/codeine/project/' + $scope.projectName + '/status');
            }
        });
    };

    $scope.select2Options = {
        'multiple': true,
        'simple_tags': true,
        'tags': $scope.tags,
        'tokenSeparators': [",", " "]
    };

    $scope.select2OptionsAllowedValues = {
        'multiple': true,
        'simple_tags': true,
        'tags': [],
        'tokenSeparators': [",", " "]
    };
}]);
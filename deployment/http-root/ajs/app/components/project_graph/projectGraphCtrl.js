(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function projectGraphCtrl($scope,$filter,$routeParams,project) {
        $scope.projectName = $routeParams.project_name;
        $scope.commandsDateToName = {};
        $scope.graphData = project.statistics;
        $scope.options = {
            axes: {
                x: {key: 'x', type: 'date', tooltipFormatter: function(x) {
                    if (angular.isDefined($scope.commandsDateToName[x.toString()])) {
                        return '   ' + $scope.commandsDateToName[x.toString()] + '   ';
                    }
                    return ' ' + $filter('date')(x,'short') + ' ';
                }},
                y: {type: 'linear'}
            },
            series: [
                {y: 'total', color: 'black',thickness : '6px', label: 'Total'},
                {y: 'fail', color: 'red',thickness : '6px', label: 'Fail'},
                {y: 'command', color: 'steelblue',thickness : '6px', label: 'Commands', type: 'column'},
            ],
            lineMode: "cardinal"
        };
        $scope.data = [];
        for (var i=0 ;i < $scope.graphData.length; i++) {
            if ($scope.graphData[i].date_long !== 0) {
                if ($scope.graphData[i].command_name !== ''){
                    $scope.commandsDateToName[new Date($scope.graphData[i].date_long).toString()] = $scope.graphData[i].command_name;
                }
                $scope.data.push({
                    x: new Date($scope.graphData[i].date_long),
                    fail: parseInt($scope.graphData[i].fail),
                    total: parseInt($scope.graphData[i].total),
                    command: $scope.graphData[i].nodes
                });
            }
        }
    }

    //// Angular Code ////
    angular.module('codeine').controller('projectGraphCtrl', projectGraphCtrl);

})(angular);
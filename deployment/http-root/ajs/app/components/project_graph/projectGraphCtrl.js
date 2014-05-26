'use strict';
angular.module('codeine').controller('projectGraphCtrl',['$scope', '$log','$filter','$routeParams', 'graphData',
    function($scope, $log, $filter,$routeParams,graphData) {
        $scope.projectName = $routeParams.project_name;
        $scope.commandsDateToName = {};
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
        for (var i=0 ;i < graphData.length; i++) {
            if (graphData[i].date_long !== 0) {
                if (graphData[i].command_name !== ''){
                    $scope.commandsDateToName[new Date(graphData[i].date_long).toString()] = graphData[i].command_name;
                }
                $scope.data.push({
                    x: new Date(graphData[i].date_long),
                    fail: parseInt(graphData[i].fail),
                    total: parseInt(graphData[i].total),
                    command: graphData[i].nodes
                });
            }
        }
    }]);
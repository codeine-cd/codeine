'use strict';
angular.module('codeine').controller('projectGraphCtrl',['$scope', '$log', '$routeParams', 'graphData',
    function($scope, $log,$routeParams,graphData) {
        $scope.projectName = $routeParams.project_name;
        $log.debug('projectGraphCtrl: current project is ' + $scope.projectName);
        $log.debug('projectGraphCtrl: graph data is  ' + angular.toJson(graphData));
        $scope.options = {
            axes: {
                x: {key: 'x', type: 'date'},
                y: {type: 'linear'}
            },
            series: [
                {y: 'fail', color: 'steelblue'},
                {y: 'total', color: 'green'},
            ]
        };
        $scope.data = [];
        for (var i=0 ;i < graphData.length; i++) {
            $scope.data.push({
                x : new Date(graphData[i].date_long),
                fail : parseInt(graphData[i].fail),
                total : parseInt(graphData[i].total)
            });
        }
    }]);
angular.module('codeine').controller('nodeCtrl',['$scope', '$log', function($scope, $log) {
    $scope.isExcludedByFilter = applySearchFilter();

    $scope.$watch("nodesFilter",function( newName, oldName ) {
            if ( newName === oldName ) {
                return;
            }
            $log.debug('nodeCtrl: nodesFilter was changed')
            applySearchFilter();
        }
    );

    function applySearchFilter() {
        var filter = $scope.nodesFilter;
        if (!filter) {
            $scope.isExcludedByFilter = false;
        }
        else {
            $scope.isExcludedByFilter = ! ($scope.node.node_name.indexOf(filter) !== -1);
        }

        if ($scope.isExcludedByFilter) {
            $scope.isExcludedByLimit = false;
        }
        else {
            if ($scope.nodesLimit[$scope.$parent.$index] > $scope.nodesVisible[$scope.$parent.$index]) {
                $scope.isExcludedByLimit = false;
                $scope.nodesVisible[$scope.$parent.$index]++;
            } else {
                $scope.isExcludedByLimit = true;
            }
        }
        $scope.node.visible = ! $scope.isExcludedByFilter;
    }
}]);
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
        $scope.node.visible = ! $scope.isExcludedByFilter;
    }
}]);
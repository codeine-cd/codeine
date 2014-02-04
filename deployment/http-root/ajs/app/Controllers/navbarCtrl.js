angular.module('codeine').controller('navbarCtrl',['$scope', '$rootScope', '$log', function($scope,$rootScope,$log) {
    $log.debug('navbarCtrl: created');
    $scope.codeineVersion =  "1.1.0";
    $scope.loggedUser =  "oshai";
    $scope.serverName =  "localhost";
}]);
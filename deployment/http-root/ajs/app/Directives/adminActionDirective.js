angular.module('codeine').directive('adminAction', ['$log','HelpConstants', function ($log, HelpConstants) {
    return {
        restrict: 'A',
        scope: true,
        link: function ($scope, element, attrs) {
            if (!$scope.app.sessionInfo.permissions.administer) {
                $log.debug('adminAction: User it not admin, will hide element');
                element.hide();
            } else {
                $log.debug('adminAction: User it admin!');
                element.show();
            }
        }
    };
}]);
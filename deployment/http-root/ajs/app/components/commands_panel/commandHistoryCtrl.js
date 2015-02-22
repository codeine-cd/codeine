(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandHistoryCtrl($scope,$log,$interval,$route,Constants,CodeineService,ApplicationFocusService) {
        /*jshint validthis:true */
        var vm = this;
        vm.projectName = $route.current.params.project_name;
        vm.tabName = $route.current.params.tab_name;
        vm.limit = 10;

        vm.historyUrl = Constants.CODEINE_WEB_SERVER + CodeineService.getApiPrefix() + '/commands-log?project=' + encodeURI(vm.projectName);
        if ($route.current.params.node_name !== undefined) {
            vm.historyUrl += '&node=' + encodeURI($route.current.params.node_name);
        }

        CodeineService.getProjectCommandHistory(vm.projectName, $route.current.params.node_name).success(function(data) {
            vm.history = data;
        });
        var maxUpdatesNotInFocus = 100;
        var intervalTriggered = 0;

        var intervalHandler = $interval(function() {
            if (!ApplicationFocusService.isInFocus() && intervalTriggered > maxUpdatesNotInFocus) {
                return;
            }
            intervalTriggered++;
            $.ajax( {
                type: 'GET',
                url: vm.historyUrl ,
                success: function(response) {
                    if  ((vm.history.length !== response.length) || (angular.toJson(vm.history) !== angular.toJson(response))) {
                        $scope.$apply(function() {
                            vm.history = response;
                        });
                    }
                },
                error:  function(err) {
                    $log.error('commandHistoryCtrl: ' + err);
                },
                dataType: 'json'
            });
        },5000,0,false);

        $scope.$on('$destroy', function() {
            $interval.cancel(intervalHandler);
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandHistoryCtrl', commandHistoryCtrl);

})(angular);
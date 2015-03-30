(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandRunningCtrl($scope,$interval,$log,$routeParams,CodeineService,Constants,ApplicationFocusService) {
        /*jshint validthis:true */
        var vm  = this;
        vm.projectName = $routeParams.project_name;
        vm.tabName = $routeParams.tab_name;
        if (typeof vm.tabName === 'undefined') {
            vm.tabName = 'main';
        }
        vm.limit = 10;

        CodeineService.getRunningCommands().success(function(data) {
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
                url: Constants.CODEINE_WEB_SERVER + CodeineService.getApiPrefix() + '/commands-status',
                success: function(response) {
                    if  ((vm.history.length !== response.length) || (angular.toJson(vm.history) !== angular.toJson(response))) {
                        $scope.$apply(function() {
                            vm.history = response;
                        });
                    }
                },
                error:  function(err) {
                    $log.error('commandRunningCtrl: ' + err);
                },
                dataType: 'json'
            });
        },5000,0,false);

        $scope.$on('$destroy', function() {
            $interval.cancel(intervalHandler);
        });
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandRunningCtrl', commandRunningCtrl);

})(angular);
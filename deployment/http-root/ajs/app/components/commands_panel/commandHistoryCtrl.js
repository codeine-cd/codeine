(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function commandHistoryCtrl($scope,$log,$timeout,$route,Constants,CodeineService,ApplicationFocusService) {
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

        var intervalHandler = $timeout(refreshFunc,5000,false);

        $scope.$on('$destroy', function() {
            $timeout.cancel(intervalHandler);
        });

      function refreshFunc() {
        if (!ApplicationFocusService.isInFocus() && intervalTriggered > maxUpdatesNotInFocus) {
          interval = $timeout(refreshFunc, 5000,false);
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
            interval = $timeout(refreshFunc, 5000,false);
          },
          error:  function(err) {
            $log.error('commandHistoryCtrl: ' + err);
            interval = $timeout(refreshFunc, 5000,false);
          },
          dataType: 'json'
        });
      }
    }

    //// Angular Code ////
    angular.module('codeine').controller('commandHistoryCtrl', commandHistoryCtrl);

})(angular);
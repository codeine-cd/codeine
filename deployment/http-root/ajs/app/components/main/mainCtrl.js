(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function mainCtrl($rootScope,$log,LoginService,CodeineConfigurationService) {
        /*jshint validthis:true */
        var vm = this;

        vm.sessionInfo = LoginService.getSessionInfo();

        $rootScope.$on('$locationChangeStart', function () {
            $log.debug('$locationChangeStart');
            vm.serverDown = false;
        });

        $rootScope.$on("$routeChangeStart", function () {
            $log.debug('$routeChangeStart');
            vm.contentLoading = true;
        });

        $rootScope.$on("$routeChangeSuccess", function (event, current, previous) {
            $log.debug('$routeChangeSuccess ' + event + current + previous);
            vm.contentLoading = false;
            var title = (LoginService.getViewAs() ? LoginService.getViewAs() + ' @' : '' );
            if (current.$$route.pageTitle) {
                if (title) {
                    title += ' ';
                }
                title += current.$$route.pageTitle;
            }
            if (CodeineConfigurationService.getGlobalConfiguration().server_name){
                if (title) {
                    title +=  ' - ';
                }
                title += CodeineConfigurationService.getGlobalConfiguration().server_name;
            }
            $rootScope.pageTitle = title;
        });

        $rootScope.$on("$routeChangeError", function (event, current, previous, rejection) {
            $log.debug('$routeChangeError - rejection = ' + rejection);
            vm.contentLoading = false;
            vm.serverDown = true;
        });



    }

    //// Angular Code ////
    angular.module('codeine').controller('mainCtrl',mainCtrl);

})(angular);
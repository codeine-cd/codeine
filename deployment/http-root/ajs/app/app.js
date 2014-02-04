angular.module('codeine', ['ngRoute', 'ngAnimate', 'ui.bootstrap','ui.select2'])
    .config(['$routeProvider','$locationProvider', '$httpProvider','$sceProvider',
        function($routeProvider,$locationProvider,$httpProvider,$sceProvider) {
            $locationProvider.html5Mode(true);
            $sceProvider.enabled(false);
            $routeProvider.
                when('/codeine', {
                    templateUrl: '/ajs/partials/projects.html'
                }).
                otherwise({
                    redirectTo: '/codeine'
                });
        }])
    .run(['$rootScope', '$log',  function($rootScope, $log) {


        $rootScope.app = {
            loading: null
        };
        $rootScope.$on('$locationChangeStart', function (event) {
            $log.debug('$locationChangeStart');
            $rootScope.app.loading = true;
        });
        $rootScope.$on('$locationChangeSuccess', function (event) {
            $log.debug('$locationChangeSuccess');
            $rootScope.app.loading = false;
        });

        /*
        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            $rootScope.app.contentLoading = true;
        });
        $rootScope.$on("$routeChangeSuccess", function (event, current, previous) {
            $rootScope.app.contentLoading = false;
        });
        $rootScope.$on("$routeChangeError", function (event, current, previous, rejection) {
            // todo: show error in this case
            $rootScope.app.contentLoading = false;
        });
        */
    }]);
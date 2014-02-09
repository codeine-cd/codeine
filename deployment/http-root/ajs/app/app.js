angular.module('codeine', ['ngRoute', 'ngAnimate', 'ui.bootstrap','ui.select2'])
    .config(['$routeProvider','$locationProvider', '$httpProvider','$sceProvider',
        function($routeProvider,$locationProvider,$httpProvider,$sceProvider) {
            $locationProvider.html5Mode(true);
            $httpProvider.interceptors.push('myHttpInterceptor');
            $sceProvider.enabled(false);
            $routeProvider.
                when('/codeine', {
                    templateUrl: '/ajs/partials/projects.html',
                    controller: 'projectsListCtrl',
                    resolve: {
                        projects : function($q,$log,CodeineService) {
                            $log.debug("resolving projects");
                            var deferred = $q.defer();
                            CodeineService.getProjects().success(function(data) {
                                $log.debug("Resolved projects: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        },
                        tabs: function($q,$log,CodeineService) {
                            $log.debug("resolving tabs");
                            var deferred = $q.defer();
                            CodeineService.getViewTabs().success(function(data) {
                                $log.debug("Resolved tabs: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        }
                    }
                }).
                when('/codeine/manage-codeine', {
                    templateUrl: '/ajs/partials/manage_codeine.html',
                    controller: 'manageCodeineCtrl',
                    resolve: {
                        tabs: function($q,$log,CodeineService) {
                            $log.debug("resolving tabs");
                            var deferred = $q.defer();
                            CodeineService.getViewTabs().success(function(data) {
                                $log.debug("Resolved tabs: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        },
                        permissions : function($q,$log,CodeineService) {
                            $log.debug("resolving permissions");
                            var deferred = $q.defer();
                            CodeineService.getPermissions().success(function(data) {
                                $log.debug("Resolved permissions: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        }
                    }
                }).
                when('/codeine/new_project', {
                    templateUrl: '/ajs/partials/new_project.html',
                    controller: 'newProjectCtrl',
                    resolve: {
                        projects : function($q,$log,CodeineService) {
                            $log.debug("resolving projects");
                            var deferred = $q.defer();
                            CodeineService.getProjects().success(function(data) {
                                $log.debug("Resolved projects: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        }
                    }
                }).
                otherwise({
                    redirectTo: '/codeine'
                });
        }])
    .run(['$rootScope', '$log','CodeineService',  function($rootScope, $log, CodeineService) {
        $rootScope.app = {
            loading: null
        };

        CodeineService.getSessionInfo().success(function(data) {
            $log.debug('run: got session info ' + angular.toJson(data));
            $rootScope.app.sessionInfo = data;
        });

        CodeineService.getGlobalConfiguration().success(function(data) {
            $log.debug('run: got configuration ' + angular.toJson(data));
            $rootScope.app.globalConfiguration = data;
        });

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
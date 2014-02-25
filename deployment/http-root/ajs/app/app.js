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
                        },
                        projects: function($q,$log,CodeineService) {
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
                when('/codeine/project/:project_name/status', {
                    templateUrl: '/ajs/partials/project_status.html',
                    controller: 'projectStatusCtrl',
                    resolve: {
                        projectConfiguration : function($q,$log,$route,CodeineService) {
                            var deferred = $q.defer();
                            CodeineService.getProjectConfiguration($route.current.params.project_name).success(function(data) {
                                $log.debug("Resolved project configuration: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        },
                        projectStatus :  function($q,$log,$route,CodeineService) {
                            var deferred = $q.defer();
                            CodeineService.getProjectStatus($route.current.params.project_name).success(function(data) {
                                $log.debug("Resolved projectStatus");
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        }
                    }
                }).
                when('/codeine/project/:project_name/node/:node_name/status', {
                    templateUrl: '/ajs/partials/node_status.html',
                    controller: 'nodeStatusCtrl',
                    resolve: {
                        nodeStatus :  function($q,$log,$route,CodeineService) {
                            var deferred = $q.defer();
                            CodeineService.getNodeStatus($route.current.params.project_name, $route.current.params.node_name).success(function(data) {
                                $log.debug("Resolved nodeStatus: " + angular.toJson(data));
                                deferred.resolve(data);
                            });
                            return deferred.promise;
                        }
                    }
                }).
                when('/codeine/project/:project_name/configure', {
                    templateUrl: '/ajs/partials/project_configure.html',
                    controller: 'projectConfigureCtrl',
                    resolve: {
                        projectConfigurationForEditing : function($q,$log,$route,CodeineService) {
                            $log.debug("resolving projects");
                            var deferred = $q.defer();
                            CodeineService.getProjectConfiguration($route.current.params.project_name).success(function(data) {
                                $log.debug("Resolved project configuration: " + angular.toJson(data));
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


        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            $log.debug('$routeChangeStart');
            $rootScope.app.contentLoading = true;
        });
        $rootScope.$on("$routeChangeSuccess", function (event, current, previous) {
            $log.debug('$routeChangeSuccess');
            $rootScope.app.contentLoading = false;
        });
        $rootScope.$on("$routeChangeError", function (event, current, previous, rejection) {
            $log.debug('$routeChangeError');
            // todo: show error in this case
            $rootScope.app.contentLoading = false;
        });

    }]);
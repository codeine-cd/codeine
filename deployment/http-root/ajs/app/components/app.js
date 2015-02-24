(function (angular,document) {
    'use strict';

    //// JavaScript Code ////

    function scrollToTop() {
        var verticalOffset = typeof(verticalOffset) !== 'undefined' ? verticalOffset : 0;
        var element = $('body');
        var offset = element.offset();
        var offsetTop = offset.top;
        $('html, body').animate({scrollTop: offsetTop}, 500, 'linear');
    }

    function getProjects(ProjectsRepository,$q) {
        var deferred = $q.defer();
        ProjectsRepository.getProjects().then(function(data) {
            deferred.resolve(data);
        }, function() {
            deferred.reject('Error - failed to get projects');
        });
        return deferred.promise;
    }

    function configFunc($compileProvider,$routeProvider,$locationProvider,$httpProvider,$sceProvider) {
        $locationProvider.html5Mode(true);
        $compileProvider.debugInfoEnabled(false);
        $httpProvider.interceptors.push('myHttpInterceptor');
        $sceProvider.enabled(false);
        $routeProvider.
            when('/codeine', {
                redirectTo: '/codeine/view/main'
            }).
            when('/codeine/view/:tab_name', {
                templateUrl: '/components/projects_list/projects.html',
                controller: 'projectsListCtrl',
                controllerAs: 'vm',
                resolve: {
                    projects : getProjects,
                    tabs: function($q,TabsRepository) {
                        return TabsRepository.getTabs();
                    }
                }
            }).
            when('/codeine/project/:project_name/command/:command_name/setup', {
                redirectTo: '/codeine/view/main/project/:project_name/command/:command_name/setup'
            }).
            when('/codeine/view/:tab_name/project/:project_name/command/:command_name/setup', {
                templateUrl: '/components/command_setup/command_setup.html',
                controller: 'commandSetupCtrl',
                controllerAs: 'vm',
                pageTitle: 'Command setup',
                resolve : {
                    command : function($q,ProjectsRepository,$route) {
                        var deferred = $q.defer();
                        ProjectsRepository.getProject($route.current.params.project_name,['runnableCommands']).then(function(project) {
                            var runnableCommands = project.runnableCommands.data;
                            for (var i=0; i < runnableCommands.length; i++) {
                                if (runnableCommands[i].name === $route.current.params.command_name) {
                                    return deferred.resolve(runnableCommands[i]);
                                }
                            }
                            deferred.reject('No such command in project ' + $route.current.params.command_name);
                        }, function() {
                            deferred.reject('Error - failed to get project runnableCommands');
                        });
                        return deferred.promise;
                    }
                }
            }).
            when('/codeine/manage-codeine', {
                templateUrl: '/components/manage_codeine/manage_codeine.html',
                controller: 'manageCodeineCtrl',
                controllerAs: 'vm',
                pageTitle: 'Manage',
                resolve: {
                    tabs: function(TabsRepository) {
                        return TabsRepository.getTabs();
                    },
                    permissions : function($q,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getPermissions().success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get project permissions');
                        });
                        return deferred.promise;
                    },
                    projects: getProjects
                }
            }).
            when('/codeine/manage/statistics', {
                templateUrl: '/components/manage_statistics/statistics.html',
                controller: 'statisticsCtrl',
                controllerAs: 'vm',
                pageTitle: 'Statistics',
                resolve: {
                    statistics: function($q,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getManageStatistics().success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get statistics');
                        });
                        return deferred.promise;
                    }
                }
            }).
            when('/codeine/new_project', {
                redirectTo: '/codeine/view/main/new_project'
            }).
            when('/codeine/view/:tab_name/new_project', {
                templateUrl: '/components/new_project/new_project.html',
                controller: 'newProjectCtrl',
                pageTitle: 'New Project',
                resolve: {
                    projects : getProjects
                }
            }).
            when('/codeine/project/:project_name', {
                redirectTo: '/codeine/view/main/project/:project_name/status'
            }).
            when('/codeine/project/:project_name/status', {
                redirectTo: '/codeine/view/main/project/:project_name/status'
            }).
            when('/codeine/view/main/project/:project_name', {
                redirectTo: '/codeine/view/main/project/:project_name/status'
            }).
            when('/codeine/view/:tab_name/project/:project_name/status', {
                templateUrl: '/components/project_status/project_status_with_menu.html',
                controller: 'projectStatusWithMenuCtrl',
                reloadOnSearch: false,
                pageTitle: 'Project Status',
                resolve: {
                    project : function($route,ProjectsRepository) {
                        return ProjectsRepository.getProject($route.current.params.project_name, ['config', 'status', 'runnableCommands']);
                    }
                }
            }).
            when('/codeine/project/:project_name/timeline', {
                redirectTo: '/codeine/view/main/project/:project_name/timeline'
            }).
            when('/codeine/view/:tab_name/project/:project_name/timeline', {
                templateUrl: '/components/project_graph/project_graph.html',
                controller: 'projectGraphCtrl',
                pageTitle: 'Project Timeline',
                resolve: {
                    project : function($route,ProjectsRepository) {
                        return ProjectsRepository.loadProjectStatistics($route.current.params.project_name);
                    }
                }
            }).
            when('/codeine/nodes/status', {
                templateUrl: '/components/project_status/internal_nodes_status.html',
                controller: 'projectStatusWithMenuCtrl',
                reloadOnSearch: false,
                pageTitle: 'Nodes Status',
                resolve: {
                    project : function(ProjectsRepository, Constants) {
                        return ProjectsRepository.getProject(Constants.CODEINE_NODES_PROJECT_NAME, [ 'status']);
                    }
                }
            }).
            when('/codeine/project/:project_name/node/:node_name', {
                redirectTo: '/codeine/view/main/project/:project_name/node/:node_name/status'
            }).
            when('/codeine/project/:project_name/node/:node_name/status', {
                redirectTo: '/codeine/view/main/project/:project_name/node/:node_name/status'
            }).
            when('/codeine/view/:tab_name/project/:project_name/node/:node_name/status', {
                templateUrl: '/components/node_status/node_status.html',
                controller: 'nodeStatusCtrl',
                pageTitle: 'Node Status',
                resolve: {
                    project : function($route,ProjectsRepository) {
                        return ProjectsRepository.getProject($route.current.params.project_name, ['config', 'runnableCommands']);
                    },
                    nodeStatus :  function($q,$route,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getNodeStatus($route.current.params.project_name, $route.current.params.node_name).success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get node status');
                        });
                        return deferred.promise;
                    }
                }
            }).
            when('/codeine/project/:project_name/node/:node_name/collector/:collector_name', {
                redirectTo: '/codeine/view/main/project/:project_name/node/:node_name/collector/:collector_name/status'
            }).
            when('/codeine/project/:project_name/node/:node_name/collector/:collector_name/status', {
                redirectTo: '/codeine/view/main/project/:project_name/node/:node_name/collector/:collector_name/status'
            }).
            when('/codeine/view/:tab_name/project/:project_name/node/:node_name/collector/:collector_name/status', {
                templateUrl: '/components/collector_status/collector_status.html',
                controller: 'collectorStatusCtrl',
                controllerAs: 'vm',
                pageTitle: 'Collector Status',
                resolve: {
                    collectorStatus :  function($q,$route,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getCollectorStatus($route.current.params.project_name, $route.current.params.node_name, $route.current.params.collector_name).success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get collector status');
                        });
                        return deferred.promise;
                    }
                }
            }).
            when('/codeine/project/:project_name/command/:command_name/:command_id', {
                redirectTo: '/codeine/view/main/project/:project_name/command/:command_name/:command_id/status'
            }).
            when('/codeine/project/:project_name/command/:command_name/:command_id/status', {
                redirectTo: '/codeine/view/main/project/:project_name/command/:command_name/:command_id/status'
            }).
            when('/codeine/view/:tab_name/project/:project_name/command/:command_name/:command_id/status', {
                templateUrl: '/components/command_status/command_status.html',
                controller: 'commandStatusCtrl',
                controllerAs: 'vm',
                pageTitle: 'Command Status',
                resolve: {
                    project :  function($route,ProjectsRepository) {
                        return ProjectsRepository.getProject($route.current.params.project_name, ['status']);
                    },
                    commandStatus :  function($q,$route,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getCommandStatus($route.current.params.project_name, $route.current.params.command_id).success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get command status');
                        });
                        return deferred.promise;
                    }
                }
            }).
            when('/codeine/project/:project_name/configure', {
                redirectTo: '/codeine/view/main/project/:project_name/configure'
            }).
            when('/codeine/view/:tab_name/project/:project_name/configure', {
                templateUrl: '/components/project_configure/project_configure.html',
                controller: 'projectConfigureCtrl',
                pageTitle: 'Project Configure',
                resolve: {
                    project :  function($route,ProjectsRepository) {
                        return ProjectsRepository.getProject($route.current.params.project_name, ['config', 'nodes_aliases']);
                    },
                    projects : getProjects
                }
            }).
            when('/codeine/user/:user_name', {
                templateUrl: '/components/user_info/user_info.html',
                pageTitle: 'User Info',
                controller: function($scope,userInfo) {
                    $scope.userInfo = userInfo;
                },
                resolve: {
                    userInfo : function($q,$route,CodeineService) {
                        var deferred = $q.defer();
                        CodeineService.getUserInfo().success(function(data) {
                            deferred.resolve(data);
                        }).error(function() {
                            deferred.reject('Error - failed to get user info');
                        });
                        return deferred.promise;
                    }
                }

            }).
            when('/', {
                redirectTo: '/codeine'
            }).
            otherwise({
                templateUrl: '/views/404.html'
            });
    }

    function runFunc($rootScope, $http, $log, $q, Constants, ApplicationFocusService, LoginService, CodeineConfigurationService) {

        $(document).on( 'scroll', function(){
            if ($(window).scrollTop() > 100) {
                $('.scroll-top-wrapper').addClass('show');
            } else {
                $('.scroll-top-wrapper').removeClass('show');
            }
        });
        $('.scroll-top-wrapper').on('click', scrollToTop);

        if (Constants.CODEINE_API_PREFIX === '/api-with-token' && localStorage.getItem('api_token')) {
            $log.debug('Run: Reading user from local storage!!! ' + localStorage.getItem('api_token'));
            $http.defaults.headers.common.api_token = localStorage.getItem('api_token');
        } else {
            $rootScope.disableToken = true;
        }

        $rootScope.app = {
            loading: null,
            initializing : true
        };

        ApplicationFocusService.init();
        var promises = [];
        promises.push(LoginService.init());
        promises.push(CodeineConfigurationService.init());

        $q.all(promises).finally(function() {
            $log.debug('run: finished initializing all services');
            $rootScope.app.initializing = false;
        });

        $rootScope.$on('$locationChangeStart', function () {
            $rootScope.app.loading = true;
        });

        $rootScope.$on('$locationChangeSuccess', function () {
            $rootScope.app.loading = false;
        });
    }

    //// Angular Code ////
    angular.module('codeine', ['ngRoute', 'ngAnimate', 'ui.bootstrap','ui.select2','ngStorage','ui.validate','ngTextcomplete','ngDistinctValues','n3-charts.linechart','angulartics', 'angulartics.google.analytics','filter.duration','cgBusy'])
    .config(configFunc)
    .run(runFunc);

})(angular,document);
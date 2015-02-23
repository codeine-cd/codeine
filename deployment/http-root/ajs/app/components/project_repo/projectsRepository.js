(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function ProjectsRepositoryFactory(CodeineService, $q, $log, CodeineProject, Constants, LoginService) {

        var _projectsRetrieveTime = new Date();
        var _pool = {};
        var _projectsArray = [];
        var _loaded = false;

        function _retrieveInstance(projectName) {
            var instance = _pool[projectName];
            if (!instance) {
                instance = new CodeineProject(projectName);
                _pool[projectName] = instance;
                _projectsArray.push(instance);
            }
            return instance;
        }

        var internal_project = new CodeineProject(Constants.CODEINE_NODES_PROJECT_NAME);

        function _search(projectName){
            if (projectName === Constants.CODEINE_NODES_PROJECT_NAME) {
                return internal_project;
            }
            return _pool[projectName];
        }

        function loadProjectConfiguration(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isConfigLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectConfiguration(projectName).success(function(data) {
                    project.setConfiguration(data);
                    deferred.resolve(project);
                }).error(function(err) {
                    $log.error('ProjectsRepository: failed to load project configuration from server',err);
                    deferred.reject(err);
                });
            });
            return deferred.promise;
        }

        function loadProjectNodesAliases(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isNodesAliasesLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectNodesAliases(projectName).success(function (data) {
                    project.setNodesAliases(data);
                    deferred.resolve(project);
                }).error(function (err) {
                    $log.error('ProjectsRepository: failed to load project nodes from server', err);
                    deferred.reject(err);
                });
            });
            return deferred.promise;
        }

        function loadProjectStatus(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isStatusLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectStatus(projectName).success(function(data) {
                    project.setStatus(data);
                    deferred.resolve(project);
                }).error(function(err) {
                    $log.error('ProjectsRepository: failed to load project status from server',err);
                    deferred.reject(err);
                });
            });
            return deferred.promise;
        }

        function loadProjectRunnableCommands(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isRunnableCommandsLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectCommands(projectName).success(function(data) {
                    project.setRunnableCommands(data);
                    deferred.resolve(project);
                }).error(function(err) {
                    $log.error('ProjectsRepository: failed to load project runnable commands from server',err);
                    deferred.reject(err);
                });
            });
            return deferred.promise;
        }

        function loadProjectStatistics(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isStatisticsLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectMonitorStatistics(projectName).success(function(data) {
                    project.setStatistics(data);
                    deferred.resolve(project);
                }).error(function(err) {
                    $log.error('ProjectsRepository: failed to load project statistics from server',err);
                    deferred.reject(err);
                });
            });
            return deferred.promise;
        }

        function getProject(name, properties) {
            var deferred = $q.defer();
            var promises = [];
            for (var i = 0 ; i < properties.length ; i++) {
                switch (properties[i])
                {
                    case 'config':
                        promises.push(loadProjectConfiguration(name));
                        break;
                    case 'status':
                        promises.push(loadProjectStatus(name));
                        break;
                    case 'nodes_aliases':
                        promises.push(loadProjectNodesAliases(name));
                        break;
                    case 'runnableCommands':
                        promises.push(loadProjectRunnableCommands(name));
                        break;
                    default:
                        throw 'No implementation for ' + properties[i];
                }
            }
            $q.all(promises).then(function(allRes) {
                deferred.resolve(allRes[0]);
            });
            return deferred.promise;
        }


        function getProjects(loadFromServer) {
            if(!loadFromServer && _loaded && ((new Date() - _projectsRetrieveTime) <= 300000)) {
                return $q.when(_projectsArray);
            }
            var deferred = $q.defer();
            CodeineService.getProjects().success(function(data) {
                _projectsRetrieveTime = new Date();
                data.forEach(function(projectData) {
                    var project = _retrieveInstance(projectData.name);
                    project.setNodesCount(projectData.nodes_count);
                });
                _loaded = true;
                deferred.resolve(_projectsArray);
            }).error(function(e) {
                $log.error('ProjectsRepository: failed to load projects from server - ' + angular.toJson(e));
                deferred.reject(e);
            });
            return deferred.promise;
        }

        function addProject(newProjectData) {
            var deferred = $q.defer();
            CodeineService.createProject(newProjectData).success(function() {
                var project = _retrieveInstance(newProjectData.project_name);
                _projectsArray.sort(function(a,b) {
                    if (a.name === b.name) {
                        return 0;
                    }
                    return a.name > b.name ? 1 : -1;
                });
                LoginService.gettingSessionInfo(true).then(function() {
                    deferred.resolve(project);
                }, function(err) {
                    deferred.reject(err);
                });
            }).error(function(err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function deleteProject(projectName) {
            var deferred = $q.defer();
            CodeineService.deleteProject(projectName).success(function() {
                deferred.resolve();
                _projectsArray.splice(_projectsArray.indexOf(_pool[projectName]),1);
                delete _pool[projectName];
            }).error(function(err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function updateProjectConfiguration(config) {
            var deferred = $q.defer();
            CodeineService.saveProjectConfiguration(config).success(function(newConfig) {
                _pool[newConfig.name].setConfiguration(newConfig);
                _pool[newConfig.name].invalidateRunnableCommands();
                deferred.resolve(_pool[newConfig.name]);
            }).error(function(err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function reloadProjectConfiguration(projectName) {
            var deferred = $q.defer();
            CodeineService.reloadProjectConfiguration(projectName).success(function(newConfig) {
                _pool[newConfig.name].setConfiguration(newConfig);
                _pool[newConfig.name].invalidateRunnableCommands();
                deferred.resolve(_pool[newConfig.name]);
            }).error(function(err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function ensureProjectsLoaded() {
            if (!_loaded) {
                return getProjects();
            }
            return $q.when();
        }

        function clearAll() {
            _projectsArray.length = 0;
            _loaded = false;
            _pool = {};
        }

        return {
            clearAll : clearAll,
            addProject : addProject,
            deleteProject : deleteProject,
            getProjects : getProjects,
            getProject : getProject,
            updateProjectConfiguration : updateProjectConfiguration,
            reloadProjectConfiguration : reloadProjectConfiguration,
            loadProjectStatistics : loadProjectStatistics,
            loadProjectConfiguration : loadProjectConfiguration,
            loadProjectNodesAliases : loadProjectNodesAliases,
            loadProjectStatus : loadProjectStatus,
            loadProjectRunnableCommands : loadProjectRunnableCommands
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('ProjectsRepository', ProjectsRepositoryFactory);

})(angular);
(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function ProjectsRepositoryFactory(CodeineService, $q, $log, CodeineProject, Constants) {

        var _pool = {};
        var _loaded = false;

        function _retrieveInstance(projectName) {
            var instance = _pool[projectName];
            if (!instance) {
                instance = new CodeineProject(projectName);
                _pool[projectName] = instance;
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

        function loadProjectNodes(projectName) {
            var deferred = $q.defer();
            ensureProjectsLoaded().then(function() {
                var project = _search(projectName);
                if (project.isNodesLoaded()) {
                    return deferred.resolve(project);
                }
                CodeineService.getProjectNodes(projectName).success(function (data) {
                    project.setNodes(data);
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
                    case 'nodes':
                        promises.push(loadProjectNodes(name));
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
            if(!loadFromServer && _loaded) {
                var projects = [];
                for (var project in _pool) {
                    projects.push(_pool[project]);
                }
                return $q.when(projects);
            }
            var deferred = $q.defer();
            CodeineService.getProjects().success(function(data) {
                var projects = [];
                data.forEach(function(projectData) {
                    var project = _retrieveInstance(projectData.name);
                    project.setNodesCount(projectData.nodes_count);
                    projects.push(project);
                });
                _loaded = true;
                deferred.resolve(projects);
            }).error(function(e) {
                $log.error('ProjectsRepository: failed to load projects from server - ' + angular.toJson(e));
                deferred.reject(e);
            });
            return deferred.promise;
        }

        function ensureProjectsLoaded() {
            if (!_loaded) {
                return getProjects();
            }
            return $q.when();
        }

        return {
            getProjects : getProjects,
            getProject : getProject,
            loadProjectStatistics : loadProjectStatistics,
            loadProjectConfiguration : loadProjectConfiguration,
            loadProjectNodes : loadProjectNodes,
            loadProjectStatus : loadProjectStatus,
            loadProjectRunnableCommands : loadProjectRunnableCommands
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('ProjectsRepository', ProjectsRepositoryFactory);

})(angular);
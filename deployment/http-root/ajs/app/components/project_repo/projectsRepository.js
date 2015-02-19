(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function ProjectsRepositoryFactory(CodeineService, $q, $log, CodeineProject) {

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

        function _search(projectName){
            return _pool[projectName];
        }

        function loadProjectConfiguration(projectName) {
            var project = _search(projectName);
            if (!project) {
                throw 'Project not loaded from server ' + projectName;
            }
            if (project.isConfigLoaded()) {
                return $q.when(project);
            }
            var deferred = $q.defer();
            CodeineService.getProjectConfiguration(projectName).success(function(data) {
                project.setConfiguration(data);
                deferred.resolve(project);
            }).error(function(err) {
                $log.error('ProjectsRepository: failed to load project configuration from server',err);
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function loadProjectNodes(projectName) {
            var project = _search(projectName);
            if (!project) {
                throw 'Project not loaded from server ' + projectName;
            }
            if (project.isNodesLoaded()) {
                return $q.when(project);
            }
            var deferred = $q.defer();
            CodeineService.getProjectNodes(projectName).success(function(data) {
                project.setNodes(data);
                deferred.resolve(project);
            }).error(function(err) {
                $log.error('ProjectsRepository: failed to load project nodes from server',err);
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function loadProjectStatus(projectName) {
            var project = _search(projectName);
            if (!project) {
                throw 'Project not loaded from server ' + projectName;
            }
            if (project.isStatusLoaded()) {
                return $q.when(project);
            }
            var deferred = $q.defer();
            CodeineService.getProjectStatus(projectName).success(function(data) {
                project.setStatus(data);
                deferred.resolve(project);
            }).error(function(err) {
                $log.error('ProjectsRepository: failed to load project status from server',err);
                deferred.reject(err);
            });
            return deferred.promise;
        }

        function loadProjectRunnableCommands(projectName) {
            var project = _search(projectName);
            if (!project) {
                throw 'Project not loaded from server ' + projectName;
            }
            if (project.isRunnableCommandsLoaded()) {
                return $q.when(project);
            }
            var deferred = $q.defer();
            CodeineService.getProjectCommands(projectName).success(function(data) {
                project.setRunnableCommands(data);
                deferred.resolve(project);
            }).error(function(err) {
                $log.error('ProjectsRepository: failed to load project runnable commands from server',err);
                deferred.reject(err);
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

        return {
            getProjects : getProjects,
            getProject : getProject,
            loadProjectConfiguration : loadProjectConfiguration,
            loadProjectNodes : loadProjectNodes,
            loadProjectStatus : loadProjectStatus,
            loadProjectRunnableCommands : loadProjectRunnableCommands
        }
    }

    //// Angular Code ////
    angular.module('codeine').factory('ProjectsRepository', ProjectsRepositoryFactory);

})(angular);
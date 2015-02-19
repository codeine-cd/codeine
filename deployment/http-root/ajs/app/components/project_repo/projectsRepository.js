(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function ProjectsRepositoryFactory(CodeineService, $q, $log, CodeineProject) {

        var _pool = {};
        var _loaded = false;

        function _retrieveInstance(projectName, projectData) {
            var instance = _pool[projectName];
            if (instance) {
                instance.setData(projectData);
            } else {
                instance = new CodeineProject(projectName, projectData.nodes_count);
                _pool[projectName] = instance;
            }
            return instance;
        }

        //function _search(projectId){
        //    return _pool[projectId];
        //}

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
                    var project = _retrieveInstance(projectData.name,projectData);
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
            getProjects : getProjects
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('ProjectsRepository', ProjectsRepositoryFactory);

})(angular);
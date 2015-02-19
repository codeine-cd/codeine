(function (angular) {
    'use strict';
    //// JavaScript Code ////
    function TabsRepositoryFactory(CodeineService, $q, $log, ProjectsTab) {

        var _pool = {};
        var _loaded = false;

        function _retrieveInstance(tabName, tabData) {
            var instance = _pool[tabName];
            if (instance) {
                instance.setData(tabData);
            } else {
                instance = new ProjectsTab(tabData);
                _pool[tabName] = instance;
            }
            return instance;
        }

        //function _search(tabName){
        //    return _pool[tabName];
        //}

        function getTabs(loadFromServer) {
            if(!loadFromServer && _loaded) {
                var tabs = [];
                for (var tab in _pool) {
                    tabs.push(_pool[tab]);
                }
                return $q.when(tabs);
            }
            var deferred = $q.defer();
            CodeineService.getViewTabs().success(function(data) {
                var tabs = [];
                tabs.push(_retrieveInstance('main',{name:'main', exp: ['.*']}));
                data.forEach(function(tabData) {
                    var tab = _retrieveInstance(tabData.name,tabData);
                    tabs.push(tab);
                });
                _loaded = true;
                deferred.resolve(tabs);
            }).error(function(e) {
                $log.error('ProjectsRepository: failed to load tabs from server - ' + angular.toJson(e));
                deferred.reject(e);
            });
            return deferred.promise;
        }

        return {
            getTabs : getTabs
        };
    }

    //// Angular Code ////
    angular.module('codeine').factory('TabsRepository', TabsRepositoryFactory);

})(angular);
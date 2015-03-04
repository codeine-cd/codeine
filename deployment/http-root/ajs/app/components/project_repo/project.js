(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineProjectFactory() {
        function CodeineProject(name) {
            this.name = name;
            this.nodes_count = 0;
            this.configuration = {};
            this.status = {};
            this.nodes_aliases = { data : []};
            this.runnableCommands = { data : []};
            this.statistics = { data : [] };
        }

        function shouldRefreshObject(obj) {
            //$log.debug('shouldRefreshObject ' + (new Date().getTime() - obj.retrieveTime) + ' ' + name + ' ' + obj.retrieveTime);
            return (new Date().getTime() - obj.retrieveTime > 300000);
        }

        CodeineProject.prototype = {
            setConfiguration : function(data) {
                data.retrieveTime = new Date().getTime();
                this.configLoaded = true;
                angular.extend(this.configuration, data);
            },
            setStatus : function(status) {
                status.retrieveTime = new Date().getTime();
                this.statusLoaded = true;
                angular.extend(this.status, status);
            },
            setStatistics : function(data) {
                this.statistics.retrieveTime = new Date().getTime();
                this.statisticsLoaded = true;
                this.statistics.data.length = 0;
                angular.copy(data,this.statistics.data);
            },
            setNodesAliases : function(nodesAliases) {
                this.nodes_aliases.retrieveTime = new Date().getTime();
                this.nodesAliasesLoaded = true;
                this.nodes_aliases.data.length = 0;
                angular.copy(nodesAliases,this.nodes_aliases.data);
            },
            setNodesCount : function(count) {
                this.nodes_count = count;
            },
            setRunnableCommands : function(runnableCommands) {
                this.runnableCommands.retrieveTime = new Date().getTime();
                this.runnableCommandsLoaded = true;
                this.runnableCommands.data.length = 0;
                angular.copy(runnableCommands,this.runnableCommands.data);
            },
            invalidateRunnableCommands : function(){
                this.runnableCommandsLoaded = false;
            },
            isConfigLoaded : function() {
                return this.configLoaded && !shouldRefreshObject(this.configuration);
            },
            isStatusLoaded : function() {
                return this.statusLoaded && !shouldRefreshObject(this.status);
            },
            isNodesAliasesLoaded : function() {
                return this.nodesAliasesLoaded && !shouldRefreshObject(this.nodes_aliases);
            },
            isRunnableCommandsLoaded : function() {
                return this.runnableCommandsLoaded && !shouldRefreshObject(this.runnableCommands);
            },
            isStatisticsLoaded : function() {
                return this.statisticsLoaded && !shouldRefreshObject(this.statistics);
            },
            cloneConfiguration : function() {
                return angular.fromJson(angular.toJson(this.configuration));
            }
        };

        return CodeineProject;
    }

    //// Angular Code ////
    angular.module('codeine').factory('CodeineProject', CodeineProjectFactory);
})(angular);

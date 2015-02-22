(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineProjectFactory() {
        function CodeineProject(name) {
            this.name = name;
            this.nodes_count = 0;
            this.configuration = {};
            this.status = {};
            this.nodes_aliases = [];
            this.runnableCommands = [];
            this.statistics = [];
        }

        CodeineProject.prototype = {
            setConfiguration : function(data) {
                this.configLoaded = true;
                angular.extend(this.configuration, data);
            },
            setStatus : function(status) {
                this.statusLoaded = true;
                angular.extend(this.status, status);
            },
            setStatistics : function(data) {
                this.statisticsLoaded = true;
                this.statistics.length = 0;
                angular.copy(data,this.statistics);
            },
            setNodesAliases : function(nodesAliases) {
                this.nodesAliasesLoaded = true;
                this.nodes_aliases.length = 0;
                angular.copy(nodesAliases,this.nodes_aliases);
            },
            setNodesCount : function(count) {
                this.nodes_count = count;
            },
            setRunnableCommands : function(runnableCommands) {
                this.runnableCommandsLoaded = true;
                this.runnableCommands.length = 0;
                angular.copy(runnableCommands,this.runnableCommands);
            },
            isConfigLoaded : function() {
                return this.configLoaded;
            },
            isStatusLoaded : function() {
                return this.statusLoaded;
            },
            isNodesAliasesLoaded : function() {
                return this.nodesAliasesLoaded;
            },
            isRunnableCommandsLoaded : function() {
                return this.runnableCommandsLoaded;
            },
            isStatisticsLoaded : function() {
                return this.statisticsLoaded;
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

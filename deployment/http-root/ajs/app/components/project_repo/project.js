(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function CodeineProjectFactory() {
        function CodeineProject(name) {
            this.name = name;
            this.configuration = {};
            this.status = {};
            this.nodes = [];
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
            setNodes : function(nodes) {
                this.setNodesCount(nodes.length);
                this.nodesLoaded = true;
                this.nodes.length = 0;
                angular.copy(nodes,this.nodes);
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
            isNodesLoaded : function() {
                return this.nodesLoaded;
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

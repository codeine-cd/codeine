(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function nodeFilter() {

        var showByName = function(query,node_name) {
            if (!query) {
                return true;
            }
            return node_name.toLowerCase().indexOf(query.toLowerCase()) !== -1;
        };

        var showByMonitor = function(selectedMonitor,monitors) {
            if (selectedMonitor === 'All Nodes') {
                return true;
            }

            if (selectedMonitor === 'Any Alert') {
                return monitors.length > 0;
            }

            return monitors.indexOf(selectedMonitor) !== -1;
        };

        var showByTags = function(tags,nodeTags) {
            for (var i=0; i < tags.length ; i++) {
                if (!tags[i].state) {
                    continue;
                }
                if (tags[i].state === 1) {
                    if (nodeTags.indexOf(tags[i].name) === -1) {
                        return false;
                    }
                } else {
                    if (nodeTags.indexOf(tags[i].name) !== -1) {
                        return false;
                    }
                }
            }
            return true;
        };

        return function(node, query, monitor, tags) {
            return (showByName(query, node.alias) && showByMonitor(monitor, node.failed_monitors) && showByTags(tags,node.tags));
        };
    }


    //// Angular Code ////
    angular.module('codeine').filter('nodeFilter', nodeFilter);

})(angular);







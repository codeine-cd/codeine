angular.module('codeine').filter('nodesFilter', [ '$log', function($log) {
    return function(nodes, query, monitor, tags) {
        var startTime = new Date();
        var res = [];
        for (var i=0; i < nodes.length ; i++) {
            if (showByName(query, nodes[i].node_alias) && showByMonitor(monitor, nodes[i].failed_monitors) && showByTags(tags,nodes[i].tags))
                res.push(nodes[i]);
        }
        var endtime = new Date();
        $log.debug("nodesFilter: took " + startTime.getTime() - endtime.getTime() + " ms");
        return res;
    };
}]);

var showByName = function(query,node_name) {
    if (!query)
        return true;
    return node_name.indexOf(query) !== -1;
}

var showByMonitor = function(selectedMonitor,monitors) {
    if (selectedMonitor === 'All Nodes')
        return true;

    if (selectedMonitor === 'Any Alert')
        return monitors.length > 0;

    return monitors.indexOf(selectedMonitor) !== -1;
}

var showByTags = function(tags,nodeTags) {
    for (var i=0; i < tags.length ; i++) {
        if (!tags[i].state) {
            continue;
        }
        if (tags[i].state === 1) {
            if (nodeTags.indexOf(tags[i].name) === -1)
                return false;
        } else {
            if (nodeTags.indexOf(tags[i].name) !== -1)
                return false;
        }
    }
    return true;
}
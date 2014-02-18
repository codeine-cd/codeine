angular.module('codeine').filter('nodesFilter', [ '$log', function($log) {
    return function(nodes, query, monitor) {
        $log.debug("nodesFilter: got " + nodes.length + " nodes, query = '" + query  + "' , monitor = '" + monitor + "'");
        if (!query)
            return nodes;
        var res = [];
        for (var i=0; i < nodes.length ; i++) {
            if (showByName(query, nodes[i].node_alias) && showByMonitor(monitor, nodes[i].failed_monitors))
                res.push(nodes[i]);
        }
        return res;
    };
}]);

var showByName = function(query,node_name) {
    return node_name.indexOf(query) !== -1;
}

var showByMonitor = function(selectedMonitor,monitors) {
    if (selectedMonitor === 'All Nodes')
        return true;

    if (selectedMonitor === 'Any Alert')
        return monitors.length > 0;

    return monitors.indexOf(selectedMonitor) !== -1;
}
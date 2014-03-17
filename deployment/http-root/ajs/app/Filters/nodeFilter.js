angular.module('codeine').filter('nodeFilter', function() {
    return function(node, query, monitor, tags) {
        return (showByName(query, node.node_alias) && showByMonitor(monitor, node.failed_monitors) && showByTags(tags,node.tags));
    };
});

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
        if (tags[i].state === undefined) {
            continue;
        }
        if (tags[i].state === 1) {
            if (nodeTags.indexOf(tags[i].name) === -1) {
                continue;
            }
            else {
                return false;
            }
        } else if (tags[i].state === 2){
            if (nodeTags.indexOf(tags[i].name) !== -1) {
                return false;
            }
        }
    }
    return true;
}
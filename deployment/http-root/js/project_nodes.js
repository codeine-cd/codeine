var nodesJson;
var versionMap;

$(document).ready( function () {
	getNodes();
});

function setupFilters() {
	filterNodes($('#monitor_drop_down').text(), $('#nodesFilter').val());
	if ($('.codeine_command').size() === 0 || readOnly) {
		$('#commandsDropdown').hide();
		$('.panel-body').find('[type=checkbox]').remove();
		$('#selecAllLabel').remove();
	}

	setAlertsCount();

	$('.nodeLink').click(function() {
		var name = encodeURIComponent($(this).data('node-name'));
		var projectName = encodeURIComponent(getProjetcName());
		if (projectName === "Codeine_Internal_Nodes_Project") {
			window.location = "/codeine-node-info?project=" + projectName + "&node-name=" + name;
		}
		else {
			window.location = "/node-info?project=" + projectName + "&node-name=" + name;
		}
	});
}

function getNodes() {
	$.ajax( {
	    type: 'GET',
	    url: '/project-nodes_json?project=' + encodeURIComponent(getProjetcName())  +'&version=' + getVersion(),
	    success: function(response) {
	    	nodesJson = jQuery.parseJSON(response);
	    	buildNodesByVersion();
	    	for (var version in versionMap) {
	    		$('#nodes_container').append($('#project_nodes_by_version').render(versionMap[version]));
	    	}
	    	setupFilters();
	    	$('#nodes_loader').fadeOut(function() {
	    		$('#nodes_container').fadeIn();
	    	});
	    }, 
	    error: function(err) {
	      console.log('error - ' + err);
	      toast("error", "Failed to get nodes", false);
	    }
    });
}


function buildNodesByVersion() {
	var a = {};
	for (var i=0; i < nodesJson.length ; i++) {
		if (a[nodesJson[i].version] === undefined) {
			a[nodesJson[i].version] = [];
		}
		a[nodesJson[i].version].push(nodesJson[i]);
	}
	versionMap = {};
	for (var item in a) {
		var temp = {};
		temp["nodes"] = a[item];
		temp["version"] = item;
		temp["num_of_nodes"] = a[item].length;
		temp["version_hash"] = getVersioHash(item);
		versionMap[item] = (temp);
	}
}

function getVersioHash(version) {
	return "v" + hashCode(version);
}


$('#nodesFilter').keyup(function (event) {
	if (event.keyCode === 27) {
		$(this).val('');
	}
	filterNodes($('#monitor_drop_down').text(),$('#nodesFilter').val());
});

$('.codeine_monitor').click( function() {
	var monitor = $(this).data('monitor-name');

	// Set active in drop down
	$('.codeine_monitor').removeClass("active");
	$(this).addClass("active");
	$('#monitor_drop_down').html(monitor);

	filterNodes(monitor,$('#nodesFilter').val());

});

function setAlertsCount() {
	var monitorsCount = {};

	$('.codeine_monitor').each( function() {
		monitorsCount[$(this).data('monitor-name')] = 0;
	});

	for (var i=0; i < nodesJson.length ; i++) {
		for (var j = 0 ; j <nodesJson[i].failed_monitors.length ; j++) {
			monitorsCount[nodesJson[i].failed_monitors[j].label]++;
		}
		if (nodesJson[i].failed_monitors.length > 0 ) {
			monitorsCount['Any Alert']++;
		}
		monitorsCount['All Nodes']++;
	}

	for (var monitor in monitorsCount) {
		if (monitorsCount[monitor] > 0 )
			$('#' + escapeSelector(monitor).replace(' ', '_')).find('span').text(monitorsCount[monitor]);
	}
}

function selectMonitor(monitor) {
	dismissAlert();
	$(monitor).click();
}

function filterNodes(monitor, filterText) {
	console.log("filterNodes started @ " + new Date().toUTCString());
	console.log("Filtering for monitor '" + monitor + "' , filter text is:" + filterText);

	$('[type=checkbox]').prop('checked', false);

	var res = [];
	if (monitor === "All Nodes") {
		if (filterText === '') {
			$('.node').show();
			for (var item in versionMap) {
				$('#' + getVersioHash(item) + '_num_of_nodes').text(versionMap[item].num_of_nodes);
			}
			return nodesJson.length;
		} else {
			$('.node').hide();
			res = nodesJson.filter(function(o){return matchNodeName(o, filterText);});
		}
	} else {
		$('.node').hide();

		res = nodesJson.filter(function(o) {
			if (monitor === "Any Alert") {
				return o.failed_monitors.length > 0;
			}

			var temp =  o.failed_monitors.filter( function(a) {
				return a.label === monitor;
			});
			return temp.length > 0;
		});
		res = res.filter(function(o){return matchNodeName(o, filterText);});
	}

	setNodesNumber(res);
	

	for (var i=0; i < res.length ; i++) {
		$('#' + escapeSelector(res[i].node_name)).show();
	}

	console.log("filterNodes finished @ " + new Date().toUTCString());

	return res.length;
} 

function setNodesNumber(res) {
	var versions = {};
	for (var item in versionMap) {
		versions[item] = 0;
	}
	for (var i=0 ; i < res.length ; i++) {
		versions[res[i].version] = versions[res[i].version] + 1;
	}
	for(var key in versions) {
		$('#' + getVersioHash(key) + '_num_of_nodes').text(versions[key]);	
	}
}


$('.codeine_command').click( function() {
	var command = $(this).data('command-name');
	console.log("Will run commad " + command);

	var parametrs = {};
	parametrs["nodes"] = getSelectedNodes();
	parametrs["command"] = command;
	parametrs["project"] = getProjetcName();

	postToUrl("/schedule-command?project=" + encodeURIComponent(getProjetcName()), parametrs);
});

function getSelectedNodes() {
	var res = [];
	var nodes = {};
	var arr = [];
	$('.panel-body').find('input:checked').each(function() {
		var obj = { 
				"peer_address" : $(this).data('peer-address'),
				"name" : $(this).data('node-name'),
				"alias" : $(this).data('node-alias')
		};
		arr.push(obj);
	});
	nodes['version'] = getParameterByName('version');
	nodes['node'] = arr;
	nodes['count'] = arr.length;
	res.push(nodes);
	return res;
}

function matchNodeName(node, filterText) {
	var regexp = new RegExp(filterText, 'i');
	return node.node_alias.match(regexp) !== null;
}





$(document).ready( function () {
	console.log("READY");
	$('.dropdown-toggle').dropdown()
	$('#monitorHelp').popover({ content: getMonitorHelpText(), html: true });
	if (filterNodes($('#monitor_drop_down').text(), $('#nodesFilter').val()) === 0) {
		displayAlert("None of your nodes has alerts, select <a href='#' onclick='selectMonitor(All_Nodes);'>'All Nodes'</a> filter to see nodes", 'warning');
	}
	
	if ($('.codeine_command').size() === 0 || readOnly) {
		$('#commandsDropdown').hide();
		$('.panel-body').find('[type=checkbox]').remove();
		$('#selecAllLabel').remove();
	}
	
	setAlertsCount();
});

$('#selectAll').change(function() {
	var value = $(this).is(":checked");
	$('.panel-body').find('[type=checkbox]:visible').prop('checked', value);
});


$('[type=checkbox').click( function() {
	resetSelectAll();
})

function resetSelectAll() {
	var allChecked = true;
	var index = 1;
	var checkbox;
	while ((checkbox = $('#checkbox_' + index)).length > 0) {
		if (checkbox.is(':visible') && !checkbox.is(':checked')) {
			allChecked = false;
			break;
		}
		index++;
	}
	$("#selectAll").prop("checked", allChecked);
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
	})
	
	for (var item in nodesJson) {
		for (var fail_monitor in nodesJson[item].failed_monitors) {
			monitorsCount[nodesJson[item].failed_monitors[fail_monitor].label]++;
		}
		if (nodesJson[item].failed_monitors.length > 0 ) {
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
	console.log("Filtering for monitor " + monitor + ' text ' + filterText);
	
	$('[type=checkbox').prop('checked', false);
	
	var res = [];
	if (monitor === "All Nodes") {
		if (filterText === '') {
			$('.node').show();
			$('#numOfNodes').text(nodesJson.length);
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
	
	$('#numOfNodes').text(res.length);
	
	for (var item in res) {
		$('#' + escapeSelector(res[item].node_name)).show();
	}
	
	return res.length;
} 



$('.codeine_command').click( function() {
	var command = $(this).data('command-name')
	console.log("Will run commad " + command);

	var parametrs = {};
	parametrs["nodes"] = getSelectedNodes();
	parametrs["command"] = command;
	parametrs["project"] = getProjetcName();
	
	postToUrl("/schedule-command?project=" + getProjetcName(), parametrs);
});

function getSelectedNodes() {
	var res = [];
	var nodes = {};
	var arr = [];
	$('.panel-body').find('input:checked').each(function() {
		var obj = { 
			"peer_address" : $(this).data('peer-address'),
			"node_name" : $(this).data('node-name'),
			"node_alias" : $(this).data('node-alias')
		}
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

function getMonitorHelpText() {
	return "Select which nodes to show.<br/><br/><strong>'Any Alert'</strong> - show nodes with alerts (no matter which monitor)<br/><br/>" +
			"<strong>'All Nodes'</strong> - show also nodes without alerts <br/><br/>" + 
			"<strong>specific monitor</strong> - show only nodes with alerts from that monitor" 
			;
	
}


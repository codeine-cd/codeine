$(document).ready( function () {
	var status = $('.peerStatus').data('peer-status');
	if (status === 'On') {
		$('.peerStatus').addClass("fa fa-link lg");
	} else {
		$('.peerStatus').addClass("fa fa-chain-broken lg");
	}
});


if ($('.codeine_command').size() === 0 || readOnly) {
	$('#node_info_navbar').hide();
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
	var obj = { 
		"peer_key" : peer_key,
		"peer_address" : peer_address,
		"name" : name,
		"alias" : alias
	};
	arr.push(obj);
	
	nodes['version'] = version;
	nodes['node'] = arr;
	nodes['count'] = arr.length;
	res.push(nodes);
	return res;
}

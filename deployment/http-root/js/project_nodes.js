
var nodesJson = [];
var versionMap;
var activeTags = [];
var queryString;
var maxTagsSizeToShow = 15;

$(document).ready( function () {
	getNodes();

	window.onpopstate  = function(event) {
		var message =
			"onpopstate: "+
			"location: " + location.href + ", " +
			"data: " + JSON.stringify(event.state) +
			"\n\n"
			;
		console.log(message);
		setTagsFromQueryString();
	};
	
	getTags();
	var filter = getUrlParameter("text-filter");
	if (filter !== undefined) {
		$("#nodesFilter").val(filter);
		filterNodes($('#monitor_drop_down').text(),$('#nodesFilter').val());
	}
});

function setTagsFromQueryString() {
	activeTags = [];
	queryString = location.search;
	if (queryString.indexOf("tags", 0) !== -1) {
		var choosenTags = getUrlParameter("tags");
		if (choosenTags !== ""){
			activeTags = choosenTags.split(",");
		}
		queryString = removeURLParameter(location.search,"tags");
	}
	console.log("activeTags=" + activeTags);
	$('.node_tag').removeClass("active");
	for (var i = 0; i < activeTags.length; i++) {
		$("#tag_" + activeTags[i]).addClass("active");
	}
	filterNodes($('#monitor_drop_down').text(), $('#nodesFilter').val());
}

function getTags() {
	$.ajax( {
	    type: 'GET',
	    url: '/api/project-tags?project=' + encodeURIComponent(getProjetcName()),
	    success: function(response) {
	    	console.dir(response);
	    	$('#tags_list').append($('#nodes_tags').render(response));
	        if (response.length > maxTagsSizeToShow) {
	      	  $("#tags_list_more").show();
	      	  var i = 0;
	      	  $('.node_tag').each(function() {
	    		if (i >= maxTagsSizeToShow){
	    			$(this).hide();
	    		}
	    		i++;
	      	  });
	      	$("#more_tags_button").click(function(){
	      		$('.node_tag').fadeIn();
	      		$("#tags_list_more").fadeOut();
	      	});
	        }
	    	setTagsFromQueryString();
	    	$('.node_tag').click(function() {
	    		var tag_name = $(this).data("tag-name");
	    		if ($(this).hasClass("active")) {
	    			$(this).removeClass("active");
	    			var index = activeTags.indexOf(tag_name);
	    			if (index > -1) {
	    				activeTags.splice(index, 1);
	    			}
	    		} else {
	    			$(this).addClass("active");
	    			activeTags.push(tag_name);
	    		}
	    		History.pushState({state:activeTags.join(",")}, document.title, queryString +"&tags=" + activeTags.join(","));
	    	});
	    }, 
	    error: function(err) {
	      console.log('error - ' + err);
	      toast("error", "Failed to get project tags", false);
	    },
	    dataType: 'json'
    });
}

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
	    url: '/api/project-nodes?project=' + encodeURIComponent(getProjetcName())  +'&version=' + encodeURIComponent(getVersion()),
	    success: function(response) {
	    	nodesJson = response;
	    	buildNodesByVersion();
	    	for (var version in versionMap) {
	    		$('#nodes_container').append($('#project_nodes_by_version').render(versionMap[version]));
	    	}
            var filter = getUrlParameter('filter');
            if ((filter) && (filter === 'Any_Alert')) {
                $('#Any_Alert').trigger("click");
            }
	    	setupFilters();
	    	$('#nodes_loader').fadeOut("fast", "swing", function() {
	    		$('#nodes_container').fadeIn("fast");
	    	});
	    	$('#nodes_container').find('[type=checkbox]').click( function() {
	    		  resetSelectAll();
	    	});
	    }, 
	    error: function(err) {
	      console.log('error - ' + err);
	      toast("error", "Failed to get nodes", false);
	    },
	    dataType: 'json'
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
	console.log("Filtering for monitor '" + monitor + "' , filter text is:" + filterText + ", active tags:" + activeTags);
	$('[type=checkbox]').prop('checked', false);

	if ((monitor === "All Nodes") && (filterText === '') && (activeTags.length == 0)) {
		$('.node').show();
		for (var item in versionMap) {
			updateNodesNumber(item, versionMap[item].num_of_nodes);
		}
		return nodesJson.length;
	}
	
	$('.node').hide();
	var res = nodesJson.filter(function(o) {return  doFilter(o, filterText, monitor);});

	setNodesNumber(res);

	for (var i=0; i < res.length ; i++) {
		$('#' + escapeSelector(res[i].node_name)).show();
	}
	return res.length;
} 

function doFilter(node,filterText,monitor) {
	 return ((matchNodeName(node, filterText)) && (matchAlerts(node,monitor)) && (matchTags(node))); 
}

function matchTags(node) {
	for(var i=0 ; i< activeTags.length ; i++) {
		if (jQuery.inArray(activeTags[i],node.tags) === -1)
			return false;
	}
	return true;
}

function matchNodeName(node, filterText) {
	var regexp = new RegExp(filterText, 'i');
	return node.node_alias.match(regexp) !== null;
}

function matchAlerts(node,monitor) {
	if (monitor === "All Nodes") {
		return true;
	}
	if (monitor === "Any Alert") {
		return node.failed_monitors.length > 0;
	}
	var temp =  node.failed_monitors.filter( function(a) {
		return a.label === monitor;
	});
	return temp.length > 0;	
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
		updateNodesNumber(key,versions[key]);
	}
}

function updateNodesNumber(version,number) {
	$('#' + getVersioHash(version) + '_num_of_nodes').text(number);
	if (number === 0) {
		$('#' + getVersioHash(version) + '_row').fadeOut('fast');
	} else {
		$('#' + getVersioHash(version) + '_row').fadeIn('fast');
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
				"peer_key" : $(this).data('peer-key'),
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






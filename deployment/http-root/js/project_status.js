$(document).ready( function () {
	console.log("READY");
	$('.dropdown-toggle').dropdown()
	if ($('.codeine_command').size() === 0 || readOnly) {
		$('#commandsNavbar').hide();
		$('.panel-body').find('[type=checkbox]').remove();
	}
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


$('.codeine_command').click( function() {
	var command = $(this).data('command-name')
	console.log("Will run commad " + command);

	var parametrs = {};
	parametrs["command"] = command;
	parametrs["project"] = getProjetcName();
	parametrs["nodes_selector"] = $('#nodes_drop_down').text();
	if ($('#numOfNodes').val() !== '') {
		parametrs["num_of_nodes"] = $('#numOfNodes').val();
	}
	parametrs["versions"] = getSelectedVersions();
	
	postToUrl("/schedule-command?project=" + getProjetcName(), parametrs);
});

function getSelectedVersions() {
	var arr = [];
	$('.panel-body').find('input:checked').each(function() {
		arr.push($(this).data('version-name'));
	});
	return arr;
}

$('.codeine_node_selector').click( function() {
	var nodesSelector = $(this).data('name');
	
	// Set active in drop down
	$('.codeine_node_selector').removeClass("active");
	$(this).addClass("active");
	$('#nodes_drop_down').html(nodesSelector);
	
	if (this.id === "Number_Nodes") {
		$('#numOfNodes').show(); 
	} else {
		$('#numOfNodes').hide();
	}
		
	
});
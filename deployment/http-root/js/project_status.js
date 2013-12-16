var chart1;
var chartDetails;
var posX;
var posY;
var divInTooltip = null;

$(document).ready( function () {

  if ($('.codeine_command').size() === 0 || readOnly) {
    $('#commandsNavbar').hide();
    $('.panel-body').find('[type=checkbox]').remove();
  }
  
	loadChart();
	
});

function loadChart() {
	chart1 = new cfx.Chart();
	chart1.setDataSource(monitors_chart_data_json);
	chart1.getAllSeries().setMarkerShape(cfx.MarkerShape.None);
	chart1.getAllSeries().getLine().setStyle(cfx.DashStyle.Dash);

	var chartDiv = document.getElementById('chartDiv');
	chart1.create(chartDiv);
}

function onGetTipDiv(args) {
	if (args.getHitType() == cfx.HitType.Point) {
		console.log("onGetTipDiv() - " + args);
		
		args.replaceDiv = false;
	}
}

function drawCommands() {
	if (commandsHistoryJson === undefined) {
		console.log("Will wait for commands data for chart");
		setTimeout(drawCommands,1000);
		return;
	}
	
	var ann = new cfx.annotation.Annotations();
	var annList = ann.getList();
	for(var command in commandsHistoryJson) {
		console.log("Command Json: " + commandsHistoryJson[command]);
		addCommandAnnotation(annList,command);
	}
	chart1.getExtensions().add(ann);
}


function addCommandAnnotation(annList,command) {
	var annText3 = new cfx.annotation.AnnotationText();
    annText3.setText(command['name']);
    command[''];
    annText3.attachElastic(2.9, 3200, 6.2, 2800);
    annText3.getBorder().setColor("#00000000");//transparent border
    annList.add(annText3);
	
}

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
	var command = $(this).data('command-name');
	console.log("Will run commad " + command);

	var parametrs = {};
	parametrs["command"] = command;
	parametrs["project"] = getProjetcName();
	parametrs["nodes_selector"] = $('#nodes_drop_down').text();
	if ($('#numOfNodes').val() !== '') {
		parametrs["num_of_nodes"] = $('#numOfNodes').val();
	}
	parametrs["versions"] = getSelectedVersions();
	
	postToUrl("/schedule-command?project=" + encodeURIComponent(getProjetcName()), parametrs);
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
$(document).ready( function () {
	$('#config_container').html($('#configure_codeine').render(codeine_configuration));
	$(".chosen-select").chosen({disable_search_threshold: 10});
	
	$("#authentication_method").val(codeine_configuration["authentication_method"]);
	$("#authentication_method").trigger("chosen:updated");
    $("#authentication_method").trigger("change");
    
    if (view_config === '') {
    	view_config = [];
    }
    drawTabs();
    
});

$('.editable').editable(function(value, settings) { 
    return(value);
 }, { 
    tooltip   : 'Click to edit',
    style  : "inherit",
    width: 150,
    onblur: 'submit'
});

function drawTabs() {
	$('#tabs_table_body').html($('#projects_tab').render(view_config));
	$('.tab_remove').click(function() {
		var tab_name = $(this).data("tab-name");
		console.log("Will remove tab '" + tab_name + "'");
		var index_to_remove = -1;
		for (var i=0; i<view_config.length ; i++) {
			if (view_config[i]["name"] === tab_name) {
				index_to_remove = i;
				break;
			}
		}
		if (index_to_remove > -1) {
			view_config.splice(index_to_remove, 1);
			drawTabs();
		}
	});
}

$('#add_tab').click(function() {
	var tab_name = $('#new_tab_name').text();
	var tab_exp = $('#new_tab_exp').text();
	var new_tab = { "name" : tab_name, "exp" : []};
	
	var values = tab_exp.split(",");
	for (var i=0; i<values.length ; i++) {
		new_tab["exp"].push(values[i]);
	}
	console.log("Adding new tab: Name: '" + tab_name + "', Exp: '" + tab_exp + "'");
	console.dir(new_tab);
	view_config.push(new_tab);
	drawTabs();
});


$('#view_config_form').submit(function(event) {
	console.log("Will submit the following view config: ");
	console.dir(view_config);
	$.ajax({
        type: 'POST',
        url: '/configure?section=view_configuration',
        data:  { data : JSON.stringify(view_config)},
        success: function () {
        	toast("success", "Codeine View Configuration was Saved",true);
        },
        error: function (jqXhr) {
        	toast("danger", "Failed to save Codeine View Configuration " + jqXhr.responseText,false);
        },
        dataType: 'json'
    });
	event.preventDefault();
});


$('#config_form').submit(function(event) {
	var formValues = getInputValues($('#config_container'));;
	console.log("Will submit the following config: ");
	console.dir(formValues);
	$.ajax({
        type: 'POST',
        url: '/configure?section=configuration',
        data:  { data : JSON.stringify(formValues)},
        success: function () {
        	toast("success", "Codeine Configuration was Saved",true);
        },
        error: function (jqXhr) {
        	toast("danger", "Failed to save Codeine Configuration " + jqXhr.responseText,false);
        },
        dataType: 'json'
    });
	event.preventDefault();
});

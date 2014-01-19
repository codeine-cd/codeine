$(document).ready( function () {
	$('#config_container').html($('#configure_codeine').render(codeine_configuration));
	
	$("#authentication_method").val(codeine_configuration["authentication_method"]);
	$("#authentication_method").trigger("chosen:updated");
    $("#authentication_method").trigger("change");
    
    if (view_config === '') {
    	view_config = [];
    }
    drawTabs();
    
    drawPermissions();
    
    $(".chosen-select").chosen({disable_search_threshold: 10});
    
    displayRelevantElementsOnly();
    
    $("#mysql_managed_by_codeine").change(function() {
    	if (this.checked) {
    		$("#mysql_dir_configure_control").show();
    		$("#mysql_bin_dir_configure_control").show();
    	} else {
    		$("#mysql_dir_configure_control").hide();
    		$("#mysql_bin_dir_configure_control").hide();
    	}
    });
    $("#mysql_managed_by_codeine").change();
    $("#authentication_method").change(function() {
    	if ($(this).val() === "WindowsCredentials") {
    		$("#roles_configure_control").show();
    	} else {
    		$("#roles_configure_control").hide();
    	}
    });
    $("#authentication_method").change();
});

function displayRelevantElementsOnly() {
	$("#directory_host_control").hide();
	
}
function drawPermissions() {
	$('#permissions_table_body').html($('#configure_permissions').render(permissions_config['permissions']));
	$(".projects_selector").select2({tags: projects , tokenSeparators: [",", " "]});
	$('.premissions_user_remove').click(function() {
		var username = $(this).data("username");
		console.log("Will remove permissions for user '" + username + "'");
		var index_to_remove = -1;
		for (var i=0; i<permissions_config['permissions'].length ; i++) {
			if (permissions_config['permissions'][i]["username"] === username) {
				index_to_remove = i;
				break;
			}
		}
		if (index_to_remove > -1) {
			permissions_config['permissions'].splice(index_to_remove, 1);
			drawPermissions();
		}
	});
} 

function drawTabs() {
	$('#tabs_table_body').html($('#projects_tab').render(view_config));
	$(".exp_selector").select2({tags: projects , tokenSeparators: [",", " "]});
	$('#tabs_table_body').find(".exp_selector").on("change", function(e) {
		var index = $(this).data("index");
    	view_config[index].exp = e.val;
	});
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
	
	$('.editable').editable(function(value, settings) { 
        return(value);
     }, { 
        tooltip   : 'Click to edit',
        style  : "inherit",
        width: 150,
        onblur: 'submit',
        callback : function(value, settings) {
        	var index = $(this).data("index");
        	view_config[index].name = value;
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
	$('#new_tab_name').text('');
	$('#new_tab_exp').find('.exp_selector').select2('data', null);
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

$('#add_user_permissions_input').keyup(function() {
	if($(this).val() != '') {
        $('#add_user_permissions').removeAttr('disabled');
    }
	else {
		$('#add_user_permissions').attr("disabled", true);
	}
});
$('#add_user_permissions').click(function() {
	var username = $('#add_user_permissions_input').val();
	$('#add_user_permissions_input').val('');
	$('#add_user_permissions').attr("disabled", true);
	var newUser = {"username":username};
	console.log("adding user " + username);
	permissions_config['permissions'].push(newUser);
	drawPermissions();
});

$('#config_permissions_save').click(function() {
	var permissions = [];
	$('.user_permissions_data').each( function() {
		var formValues = getInputValues(this);
		formValues['username'] = $(this).data('username');
		permissions.push(formValues);
	});
	console.log("Will submit the following permissions config: ");
	console.dir(permissions);
	var postObject = {};
	postObject['permissions'] = permissions;
	$.ajax({
        type: 'POST',
        url: '/configure?section=permissions',
        data:  { data : JSON.stringify(postObject)},
        success: function () {
        	toast("success", "Permissions Configuration was Saved",true);
        },
        error: function (jqXhr) {
        	toast("danger", "Failed to save Permissions Configuration " + jqXhr.responseText,false);
        },
        dataType: 'json'
    });
});

$('#config_form').submit(function(event) {
	var formValues = getInputValues($('#config_container'));
	if (formValues["roles"] !== undefined) {
		formValues["roles"] = formValues["roles"].split(",");
	}
	formValues['mysql'] = [];
	formValues['mysql'][0] = {};
	formValues['mysql'][0].host = formValues['mysql_host'];
	formValues['mysql'][0].port = formValues['mysql_port'];
	formValues['mysql'][0].dir = formValues['mysql_dir'];
	formValues['mysql'][0].bin_dir = formValues['mysql_bin_dir'];
	formValues['mysql'][0].user = formValues['mysql_user'];
	formValues['mysql'][0].password = formValues['mysql_password'];
	formValues['mysql'][0].managed_by_codeine = formValues['mysql_managed_by_codeine'];
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

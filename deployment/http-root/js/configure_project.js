var totalMonitors = 0;
var totalCommands = 0;
var newParameterIndex = 1000;
var enterNewNodeConst = "Enter New Node";
var enterNewMailConst = "Enter New Email";
var codeine_env_vars = ['CODEINE_OUTPUT_FILE', 'CODEINE_PROJECT_NAME', 'CODEINE_NODE_NAME'];
var codeine_env_vars_no_output = ['CODEINE_PROJECT_NAME', 'CODEINE_NODE_NAME'];

$(document).ready( function () {
	$("#node_discovery_strategy").change(function() {
		$('.nodes_list').addClass("hidden");
		$('#monitors_panel').removeClass("hidden");
		$('#commands_panel').removeClass("hidden");
		var value = $(this).find('option:selected').val();
		console.log(value + " selected");
		if (value === "Script") {
			$('#nodes_script').removeClass("hidden");
			$('#tags_script').removeClass("hidden");
		} else if (value === "Configuration") {
			$('#nodes_table').removeClass("hidden");
		} else if (value === "Reporter") {
			$('#monitors_panel').addClass("hidden");
			$('#commands_panel').addClass("hidden");
		}
    });
	
	$("#node_discovery_strategy").val(project["node_discovery_startegy"]);
	$("#node_discovery_strategy").trigger("chosen:updated");
    $("#node_discovery_strategy").trigger("change");
    
    drawMonitors();
	
    drawCommands();
    
	drawNodes();
	
	drawMailPolicy();
	
	registerAccordionHandlers($(".accordion"));
	
	registerSaveHandler();
	
	registerTextAreaAutocomplete($('.codeine_script'));//discovery, version
	registerTextAreaAutocompleteNoOutput($('.codeine_script_no_output'));//commands, monitors
	
	$(".chosen-select").chosen({disable_search_threshold: 10});
	
});

function registerSaveHandler() {
	$('#save_button').click(function () {
		console.log("Save clicked");
		var newProjectConf = setNewProjectConfValues();
		sendNewConfToServer(newProjectConf,goToProjectStatus);
		
	});
	
	$('#apply_button').click(function () {
	  console.log("Apply clicked");
	  var newProjectConf = setNewProjectConfValues();
	  sendNewConfToServer(newProjectConf);
  });
}

function goToProjectStatus() {
  document.location = "/project-status?project=" + encodeURIComponent(getProjetcName());
}

function sendNewConfToServer(newProjectConf, callback) {
	$.ajax(
            {
                type: 'POST',
                url: '/configure-project?project=' + getProjetcName(),
                data:  { data : JSON.stringify(newProjectConf, undefined, 2) },
                success: function () {
                	toast("success", "Project configuration was saved",true);
                	if (callback !== undefined)
                	  callback();
                },
                error: function (jqXhr) {
                	toast("danger", "Failed to save project configuration " + jqXhr.responseText,false);
                },
                dataType: 'json'
            }
        );
} 

function setNewProjectConfValues() {
	var newProjectConf = {};
	newProjectConf["name"] = project["name"];
	var nodes_discovery_strategy = $("#node_discovery_strategy").find('option:selected').val();
	newProjectConf["node_discovery_startegy"] = nodes_discovery_strategy;
	newProjectConf["version_detection_script"] = $('#version_detection_script').val();
	setNewProjectMailNotifications(newProjectConf);
	switch (nodes_discovery_strategy) 
	{
		case "Reporter":
			break;
		case "Script":
			newProjectConf["nodes_discovery_script"] = $('#nodes_discovery_script').val();
			newProjectConf["tags_discovery_script"] = $('#tags_discovery_script').val();
			setNewProjectMonitorsAndCommands(newProjectConf);
			break;
		case "Configuration":
			newProjectConf["nodes_info"] = [];
			$('#nodes_table_body').find('tr').each( function() {
				var nameElm = $(this).children().first();
				var aliasElm = nameElm.next();
				var tagsElm = aliasElm.next();
				
				if ((nameElm.text() === enterNewNodeConst) || (aliasElm.text() === enterNewNodeConst)) {
					return;
				}
				newProjectConf["nodes_info"].push({ name: nameElm.text(), alias: aliasElm.text(), tags: $(tagsElm).find('.tags_selector').select2("val")});
			});
			setNewProjectMonitorsAndCommands(newProjectConf);
			break;
		default:
			console.log("Unknown nodes discovery strategy - " + nodes_discovery_strategy);
			toast('danger','Fatal error: Unknown nodes discovery strategy - ' + nodes_discovery_strategy,true);
			return;
	}
	
	return newProjectConf;
}

function setNewProjectMailNotifications(newProjectConf) {
	console.log("Adding mail notifications:");
	newProjectConf["mail"] = [];
	$('#mail_policy_table_body').find('tr').each( function() {
		var userElm = $(this).children().first();
		var intensity = userElm.next().find('select').val();
		if (userElm.text() === enterNewMailConst) {
			return;
		}
		var obj = { user: userElm.text(), intensity: intensity};
		newProjectConf["mail"].push(obj);
		console.dir(obj);
	});
}

function setNewProjectMonitorsAndCommands(newProjectConf) {
	setNewProjectMonitors(newProjectConf);
	setNewProjectCommands(newProjectConf);
}
function setJsonDataInput(elm, property, command){
  var value = elm.find('[id^="command_'+property+'_index_"]').val();
  if (value !== '' && value !== undefined) {
    command[property] = value;
  }
}
function setNewProjectCommands(newProjectConf) {
	newProjectConf["commands"] = [];
	console.log("Adding commands:");
	
	$('#commands').find('form').each(function() {
		var command = {};
		command["name"] = $(this).find('[id^="command_name_index_"]').val();
		
		command["prevent_override"] = $(this).find('[id^="command_prevent_override_index_"]').is(':checked');
		command["stop_on_error"] = $(this).find('[id^="command_stop_on_error_index_"]').is(':checked');
		
		setJsonDataInput($(this), "credentials", command);
        setJsonDataInput($(this), "command_strategy", command);
		setJsonDataInput($(this), "duration", command);
		setJsonDataInput($(this), "concurrency", command);
		setJsonDataInput($(this), "error_percent_val", command);
		setJsonDataInput($(this), "duration_units", command);
		setJsonDataInput($(this), "ratio", command);
		
		command["script_content"] = $(this).find('[id^="command_script_content_index_"]').val();
		
		command["parameters"] = getCommandParameters($(this));
		
		newProjectConf["commands"].push(command);
		
		console.dir(command);
	});
}

function getCommandParameters(commandElm) {
	var parameters = [];
	commandElm.find('[id^="command_parameter_row_index"]').each(function() {
		var parameter = {};
		parameter["name"] = $(this).find('[id^="command_patameter_name_index_"]').val();
		parameter["type"] = $(this).data("type");
		
		var description = $(this).find('[id^="command_patameter_description_index_"]').val();
		if (description !== '') {
			parameter["description"] = description;
		}
		
		var validation_expression = $(this).find('[id^="command_patameter_validation_expression_index_"]').val();
		if ((validation_expression !== '') && (validation_expression !== undefined)) {
			parameter["validation_expression"] = validation_expression;
		}
		
		var values = $(this).find('[id^="command_patameter_values_index_"]').val();
		if ((values !== '') && (values !== undefined)) {
			parameter["allowed_values"] = values.split(",");
		}
		
		var defaultValue;
		if (parameter["type"] === "Boolean") {
			defaultValue = $(this).find('[id^="command_patameter_default_val_index_"]').is(':checked');
		} else {
			defaultValue = $(this).find('[id^="command_patameter_default_val_index_"]').val();	
		}
		if (defaultValue !== '') {
			parameter["default_value"] = defaultValue;
		}
		parameters.push(parameter);
	});
	return parameters;
}

function setNewProjectMonitors(newProjectConf) {
	newProjectConf["monitors"] = [];
	console.log("Adding monitors:");
	$('#monitors').find('form').each(function() {
		var monitor = {};
		monitor["name"] = $(this).find('[id^="monitor_name_index_"]').val();
		
		var minInterval = $(this).find('[id^="monitor_interval_index_"]').val();
		if (minInterval !== '') {
			monitor["minInterval"] = minInterval;
		}
		
		var credentials = $(this).find('[id^="monitor_credentials_index_"]').val();
		if (credentials !== '') {
			monitor["credentials"] = credentials;
		}
		
		monitor["notification_enabled"] = $(this).find('[id^="monitor_notification_enabled_index_"]').is(':checked');
		monitor["script_content"] = $(this).find('[id^="monitor_script_content_index_"]').val();
		
		newProjectConf["monitors"].push(monitor);
		
		console.dir(monitor);
	});
}


function drawCommands() {
	totalCommands = project["commands"].length;
	$("#commands").html($("#configure_project_command").render(project["commands"]));
	
	$('#add_command').click(function() {
		totalCommands = addItem(totalCommands, "command");
	});
	
	registerItemRemoveHandlers('command_parameter', $('.parameter_remove'));
	
	registerItemRenameHandler($('[id^=command_name]'));
	
	registerCommandStrategyHandler($('[id^=command_strategy_index_]'));

	registerStopOnErrorHandler($('[id^=command_stop_on_error_index_]'));
	
	registerAddParameterHandler($('[id^=add_parameter_]'));
	
	
	registerCommandRemoveHandlers($('.command_remove'));
}

function drawMonitors() {
	totalMonitors = project["monitors"].length;
	$("#monitors").html($("#configure_project_monitor").render(project["monitors"]));
	
	registerItemRenameHandler($('[id^=monitor_name]'));
	
	$('#add_monitor').click(function() {
		totalMonitors = addItem(totalMonitors, "monitor");
	});
	
	registerMonitorRemoveHandlers($('.monitor_remove'));
}

function registerTextAreaAutocomplete(elm) {
	registerTextAreaAutocompleteInternal(elm, codeine_env_vars);
}
function registerTextAreaAutocompleteNoOutput(elm) {
	registerTextAreaAutocompleteInternal(elm, codeine_env_vars_no_output);
}
function registerTextAreaAutocompleteInternal(elm, list) {
	elm.each(function() {
		$(this).textcomplete([
			{ 
			    match: /\$(\w*)$/,
			    search: function (term, callback) {
			        callback($.map(list, function (env_var) {
			            return env_var.indexOf(term) === 0 ? env_var : null;
			        }));
			    },
			    index: 1,
			    template: function (value) {
		            return '<img src="resources/img/codeine_16x16.png"></img> ' + value;
		        },
			    replace: function (env_var) {
			        return '$' + env_var + ' ';
			    }
			}
		]);
	});
      
}

function registerAddParameterHandler(elm) {
  elm.click(function() {
    var type =  this.id.substring(this.id.toString().lastIndexOf("_")+1);
    var obj = { type : type};
    var index = $(this).data("index");
    var tmp = [];
    tmp.push(obj);
    var elm = $("#configure_command_parameter").render(tmp);
    elm = elm.replace(/index__/g,"index_" + newParameterIndex++ + "_");
    $("#command_parameters_" + index).append(elm);
    
    registerItemRemoveHandlers('command_parameter',$("#command_parameters_" + index).find('.parameter_remove').last());
    
    registerHelpElements($("#command_parameters_" + index).find('.command_parameter').last());
  });
}

function addItem(counter,itemType) {
	var elm = $("#configure_project_" + itemType ).render({name:"new_" + itemType + "_" + counter, notification_enabled : true});
	elm = elm.replace(/index_/g,"index_" + counter);
	elm = elm.replace(/data-index=\"\"/g," data-index='" + counter + "' ");
	$("#" + itemType +"s").append(elm);
	
	registerItemRenameHandler($('[id^=' + itemType +'_name_index_' + counter + ']'));
	registerTextAreaAutocompleteNoOutput($('[id=' + itemType +'_script_content_index_' + counter  + ']'));

	if (itemType === "command")
	{
	  registerAddParameterHandler($('#command_row_index_' +  counter).find('[id^=add_parameter_]'));
	  registerCommandStrategyHandler($('[id^=command_strategy_index_' + counter + ']'));
	  $('[id^=command_strategy_index_' + counter + ']').chosen({disable_search_threshold: 10});
	}
	
	registerItemRemoveHandlers(itemType,$("#" + itemType +"s").find('.' + itemType + '_remove').last());
		
	registerAccordionHandlers($("#" + itemType +"s").find('.accordion').last());
	$("#" + itemType +"s").find('.accordion').last().find('.collapse').collapse('show');
	
	registerHelpElements($("#" + itemType +"s").find('.accordion').last());
	
	counter++;
	return counter;
}

function registerItemRenameHandler(elm) {
	elm.change(function() {
		var value = $(this).val();
		$('#accordion_' + this.id).find('span').text(value);
	});
}
function registerCommandStrategyHandler(elm) {
  elm.change(function() {
    var index = $(this).data("index");
    var command = { command_strategy: $(this).val() };
    if (project.commands[index] !== undefined) {
      project.commands[index]["command_strategy"] = $(this).val();
      command = project.commands[index];
    }
    var innerHtml = $("#command_startegy").render(command);
    innerHtml = innerHtml.replace(/index_/g,"index_" + index);
    innerHtml = innerHtml.replace(/data-index=\"\"/g," data-index='" + index + "' ");
    $("#command_strategy_info_index_" + index).html(innerHtml);
    $("#command_strategy_info_index_" + index).find(".chosen-select").chosen({disable_search_threshold: 10});
    registerStopOnErrorHandler($('[id^=command_stop_on_error_index_' + index + ']'));
  });
}
function registerStopOnErrorHandler(elm) {
  elm.change(function() {
    var index = $(this).data("index");
    if ($(this).is(':checked')) {
      $('#command_errorPercent_index_' + index).removeClass('hidden');
    } else {
      $('#command_errorPercent_index_' + index).addClass('hidden');
    }
  });
};
function registerCommandRemoveHandlers(elm) {
	registerItemRemoveHandlers('command', elm);
}

function registerMonitorRemoveHandlers(elm) {
	registerItemRemoveHandlers('monitor', elm);
}

function registerItemRemoveHandlers(item, elm) {
	elm.click(function() {
		var index = $(this).data("index");
		console.log("Will remove " + item + " " + index);
		$('#' + item +'_row_' +  index).remove();
	});
}

function registerAccordionHandlers(elm) {
	elm.on("show",function(event){
	    var collapse_element = event.target;
	    console.log(collapse_element.id + " show");
	    $(collapse_element).parent().find('.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle');
	});

	elm.on("hide",function(event){
	    var collapse_element = event.target;
	    console.log(collapse_element.id + " hide");
	    $(collapse_element).parent().find('.fa-minus-circle').removeClass('fa-minus-circle').addClass('fa-plus-circle');
	    
	});
}


function add_new_mail_line() {
	$("#add_mail_policy_table_body").html($("#mail_policy_table_row").render({}));
	$("#add_mail_policy_table_body").find("td").last().find("a").prop("title", "Add Email").removeClass("mail_remove").addClass("mail_add").removeClass("btn-danger").addClass("btn-info").find("i").removeClass("fa-times").addClass("fa-plus");
	
	make_mail_element_editable($("#add_mail_policy_table_body").find("tr").last().find('.editable'));
	$("#add_mail_policy_table_body").find("tr").last().find(".chosen-select").chosen({disable_search_threshold: 10});
	
	$('.mail_add').click(function() {
		var valid = true;
		var values = [];
		$(this).parent().parent().find('.editable').each(function() {
			if ($(this).html() === enterNewMailConst) {
				console.log("ERROR");
				if (valid === true) {
					toast('danger','Please enter a new email and intensity before adding another one',true);
				}
				valid = false;
			}
			values.push($(this).html());
		});
		if (valid) {
			$(this).parent().parent().remove();
			$("#mail_policy_table_body").append($("#mail_policy_table_row").render({ user : values[0], intensity : values[1]}));
			$("#mail_policy_table_body").find("tr").last().find(".chosen-select").chosen({disable_search_threshold: 10});
			make_mail_element_editable($("#mail_policy_table_body").find("tr").last().find('.editable'));
			registerIconRemoveHandler($("#mail_policy_table_body").find("td").last().find("a"));
			add_new_mail_line();
		}
    });
}

function add_new_node_line() {
	$("#add_node_table_body").html($("#nodes_table_row").render({}));
	$("#add_node_table_body").find("td").last().find("a").prop("title", "Add Node").removeClass("node_remove").addClass("node_add").removeClass("btn-danger").addClass("btn-info").find("i").removeClass("fa-times").addClass("fa-plus");
	make_node_element_editable($("#add_node_table_body").find("tr").last().find('.editable'));
	$("#add_node_table_body").find(".tags_selector").select2({tags: [], tokenSeparators: [",", " "]});
	
	$('.node_add').click(function() {
		var valid = true;
		var values = [];
		$(this).parent().parent().find('.editable').each(function() {
			if ($(this).html() === enterNewNodeConst) {
				console.log("ERROR");
				if (valid === true) {
					toast('danger','Please enter a new node name and alias before adding another one',true);
				}
				valid = false;
			}
			values.push($(this).html());
		});
		if (valid) {
			$("#nodes_table_body").append($("#nodes_table_row").render({ name : values[0], alias : values[1], tags: $(this).parent().parent().find(".tags_selector").select2("val")}));
			make_node_element_editable($("#nodes_table_body").find("tr").last().find('.editable'));
			$("#nodes_table_body").find(".tags_selector").select2({tags: [], tokenSeparators: [",", " "]});
			registerIconRemoveHandler($("#nodes_table_body").find("td").last().find("a"));
			add_new_node_line();
		}
    });
}



function make_node_element_editable(elm) {
	make_element_editable(elm, enterNewNodeConst);
}

function make_mail_element_editable(elm) {
	make_element_editable(elm, enterNewMailConst);
}

function make_element_editable(elm,placeholder) {
	elm.editable(function(value, settings) { 
	     return(value);
	  }, { 
	     tooltip   : 'Click to edit',
	     style  : "inherit",
	     width: 150,
	     onblur: 'submit',
	     placeholder: placeholder
	 });
}

function registerIconRemoveHandler(element) {
	element.click(function() {
    	$(this).parent().parent().remove();
    });
}

function drawNodes() {
	$("#nodes_table_body").html($("#nodes_table_row").render(project["nodes_info"]));
	
	make_node_element_editable($("#nodes_table_body").find('.editable'));
	$("#nodes_table_body").find(".tags_selector").select2({tags: [], tokenSeparators: [",", " "]});
	registerIconRemoveHandler($('.node_remove'));
	add_new_node_line();
}

function drawMailPolicy() {
	$("#mail_policy_table_body").html($("#mail_policy_table_row").render(project["mail"]));
	$('.intensity_selector').each(function() {
		var value = $(this).data("selected");
		$(this).val(value);
	});
	make_mail_element_editable($("#mail_policy_table_body").find('.editable'));
	registerIconRemoveHandler($('.mail_remove'));
	add_new_mail_line();
	
}






var parametersState = {};

$(document).ready(function(){
	$(".chosen-select").chosen({disable_search_threshold: 10});

	// config values
	updateUiInput('duration');
	updateUiInput('concurrency');
  updateUiInput('error_percent_val');
  updateUiSelect('ratio');
  updateUiSelect('duration_units');
  updateUiSelect('command_strategy');
  updateUiCheckbox('stop_on_error');
	if (!command['prevent_override']) {
	  $('#configuration').removeClass("hidden");
	} else {
	  $('#configuration').addClass("hidden");
	}
	if ($(".node_row").length <= 1) {
		$(".immediatelyControl").addClass("hidden");
	}
	$('.progressiveControl').addClass('hidden');
	
	if (command["parameters"].length == 0){
	  $('#panel_parameters').addClass('hidden');
	}
	else {
  	renderTemplate('command_parameter', $("#parameters") , command["parameters"], function() {
  	  $("[id^=param_selection_]").chosen({disable_search_threshold: 10});
  	  
  	  $('[id^=param_string_]').each(function() {
  	    validateCommandParam(this);
  	  });
  	  
  	  $('[id^=param_string_]').change(function() {
  	    validateCommandParam(this);
  	  });
  	  
  	  $('[id^=help_param').each(function() {
  	    $(this).popover({ content: $(this).data("help-content"), html: false });
  	  });
  	});
	}
	setUI();
});


function validateCommandParam(input) {
  var expr = $(input).data("validation-exp");
  var value = $(input).val();
  var regExp = new RegExp(expr,"g");
  if (!regExp.test(value)) {
    console.log("Value '" + value + "' is not ok! [Req Exp = " + expr + "]");
    parametersState[input.id] = false;
    $(input).closest('.control-group').removeClass("success").addClass("error");
  } else {
    parametersState[input.id] = true;
    $(input).closest('.control-group').removeClass("error").addClass("success");
  } 
  $('#submitCommand').prop('disabled', !isAllParamsValid());
}

function isAllParamsValid() {
  for (var param in parametersState) {
    if (parametersState[param] === false) 
      return false;
  }
  return true;
}

function updateUiCheckbox(id){
  if (command[id]){
    $("#" + id).prop('checked', true);
  }
}
function updateUiInput(id){
  if (command[id]){
    $("#" + id).val(command[id]);
  }
}
function updateUiSelect(id){
  if (command[id]){
    $("#" + id).val(command[id]);
    $("#" + id).trigger("chosen:updated");
    $("#" + id).trigger("change");
  }
}

$('#command_strategy').change(function() {
	setUI();
});

$('#submitCommand').click(function() {
	var parameters = {};
	parameters["command_info"] = {};
	parameters["command_info"]["parameters"] = [];
	var formValues = getInputValues($('#command_info'));;
	for (var item in formValues) {
	  if (item.toString().indexOf("param") === 0) {
	    var temp = {};
	    temp["value"] = formValues[item];
	    temp["name"] = $('#' + item).data("name");
	    parameters["command_info"]["parameters"].push(temp); 
	  } else {
	    parameters["command_info"][item] = formValues[item]; 
	  } 
	}
	parameters["nodes"] = nodes;
	parameters["command_info"]["script_content"] = command["script_content"];
	console.log('will submit command:');
	console.dir(parameters);
	postToUrl('/command-nodes_json?redirect=true',parameters);
});

$('#stop_on_error').change(function() {
	if ($('#stop_on_error').is(':checked')) {
		$('#errorPercent').removeClass('hidden');
	} else {
		$('#errorPercent').addClass('hidden');
	}
});

$(".accordion").on("show",function(event){
    collapse_element = event.target;
    console.log(collapse_element.id + " show");
    $(collapse_element).parent().find('.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle');
});

$(".accordion").on("hide",function(event){
    collapse_element = event.target;
    console.log(collapse_element.id + " hide");
    $(collapse_element).parent().find('.fa-minus-circle').removeClass('fa-minus-circle').addClass('fa-plus-circle');
    
});

function setUI() {
	var value = $('#command_strategy').find('option:selected').val();
	console.log('current duration ' + value);
	if (value.toLowerCase() === 'progressive') {
		$('.progressiveControl').removeClass('hidden').fadeIn('slow');
		$('.immediatelyControl').fadeOut('slow');
		if ($('#stop_on_error').is(':checked')) {
	    $('#errorPercent').removeClass('hidden');
	  } else {
	    $('#errorPercent').addClass('hidden');
	  }
	} else {
		$('.progressiveControl').fadeOut('slow');
		$('.immediatelyControl').fadeIn('slow');
	} 
}
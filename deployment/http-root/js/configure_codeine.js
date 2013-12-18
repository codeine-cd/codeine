$(document).ready( function () {
	$('#config_container').html($('#configure_codeine').render(codeine_configuration));
	$(".chosen-select").chosen({disable_search_threshold: 10});
	
	$("#authentication_method").val(codeine_configuration["authentication_method"]);
	$("#authentication_method").trigger("chosen:updated");
    $("#authentication_method").trigger("change");
});

$('#config_form').submit(function(event) {
	var formValues = getInputValues($('#config_container'));;
	console.log("Will submit the following config: ");
	console.dir(formValues);
	$.ajax({
        type: 'POST',
        url: '/configure',
        data:  { data : JSON.stringify(formValues)},
        success: function () {
        	toast("success", "Codeine configuration was saved",true);
        },
        error: function (jqXhr) {
        	toast("danger", "Failed to save Codeine configuration " + jqXhr.responseText,false);
        },
        dataType: 'json'
    });
	event.preventDefault();
});

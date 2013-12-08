$(document).ready( function () {
	
	$('#new_project').find('form').find('input').change(function() {
		validate();
	});
	
	$('#selected_project').change(function() {
		validate();
	});
	
	$(".chosen-select").chosen({disable_search_threshold: 10});
	
	validate();
	
	$("#create_button").click(function() {
    console.log("creating new project");
    var newVal = getInputValues($('#new_project'));
    var conf = $('input[name=optionsRadios]:checked', '#new_project').val();
    newVal['type'] = (conf === 'copy_config') ? 'Copy' : 'Empty'; 
    $.ajax(
        {
            type: 'POST',
            url: '/new-project',
            data:  { data : JSON.stringify(newVal) },
            success: function () {
              console.log("success created new project");
              window.location = "/configure-project?project=" + encodeURIComponent($("#project_name").val());
            },
            error: function (jqXhr) {
              toast("danger", "Failed to create new project: " + jqXhr.responseText,false);
            },
            dataType: 'json'
        }
    );
  });
});

function validate() {
	var valid = true;
	var newName = $('#project_name').val();
	var conf = $('input[name=optionsRadios]:checked', '#new_project').val();
	
	if (newName === '') {
		valid = false;
		$('#project_name').closest('.control-group').removeClass("success").addClass("error");
	} else {
		$('#project_name').closest('.control-group').removeClass("error").addClass("success");
	}
	
	if (conf === 'copy_config'){
		var selectedProject = $('#selected_project').find(':selected').val();
		if (selectedProject === '') {
			valid = false;
		} 
	}
	$('#create_button').prop('disabled', !valid);
}
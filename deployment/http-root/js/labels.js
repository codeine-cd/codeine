$(document).ready( function () {
	console.log("READY");
	setPermissions();
	renderTemplate('label', $("#labels") , labelsJson, setPermissions);
});

function setPermissions() {
  if (readOnly) {
    $('.change_element').hide();
  } else {
    $('.change_element').show();
  } 
}

function addLabel() {

	var data = {};
	data['label'] = $('#label').val();
	data['version'] = $('#version').val();
	data['description'] = $('#description').val();
	data['project'] = $('#project').val();
  
	$.ajax ({
      type: "POST",
      url: "/labels?project=" + encodeURIComponent(getParameterByName('project')),
      dataType: 'json',
      data: JSON.stringify(data),
      success: function () {
    	  console.log("good");
    	  displayAlert("Label '" + data['label'] + "' was added",'success');
    	  labelsJson.push(data);
    	  resetInput();
    	  renderTemplate('label', $("#labels") , labelsJson, setPermissions);
    	  
      },
      error: function (err) {
    	  console.log("bad - " + err);
    	  toast("danger", "Failed to add label '" + data['label'] + "'",false);
      }
  });
}	

function resetInput() {
	$('#label').val('');
	$('#version').val('');
	$('#description').val('');
	$('#project').val('');
}

function getLabelIndex(label) {
	for(var item in labelsJson) {
		if (labelsJson[item].label === label)
			return item;
	}
	return -1;
}

function deleteLabel(projectName, label)
{
	$.ajax ({
      type: "DELETE",
      url: "/labels?project=" + encodeURIComponent(projectName) + "&label=" + encodeURIComponent(label),
      dataType: 'json',
      success: function () {
    	  console.log("good");
    	  toast("success", "Label '" + label + "' was removed",true);
    	  var index = getLabelIndex(label);
    	  if (index !== -1) {
	    	  labelsJson.splice(index,1);
	    	  renderTemplate('label', $("#labels") , labelsJson);
    	  }
      },
      error: function (err) {
    	  console.log("bad - " + err);
    	  toast("danger", "Failed to add label '" + data['label'] + "'",false);
      }
	});
}
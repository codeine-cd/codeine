$(document).ready( function () {
	console.log("READY");
	renderTemplate('label', $("#labels") , labelsJson);
});

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
    	  renderTemplate('label', $("#labels") , labelsJson);
      },
      error: function (err) {
    	  console.log("bad - " + err);
    	  displayAlert("Failed to add label '" + data['label'] + "'",'error');
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
    	  displayAlert("Label '" + label + "' was removed",'success');
    	  var index = getLabelIndex(label)
    	  if (index !== -1) {
	    	  labelsJson.splice(index,1);
	    	  renderTemplate('label', $("#labels") , labelsJson);
    	  }
      },
      error: function (err) {
    	  console.log("bad - " + err);
    	  displayAlert("Failed to remove label '" + label + "'",'error');
      }
	});
}
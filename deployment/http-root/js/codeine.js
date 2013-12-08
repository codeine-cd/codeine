function formatTemplatePath(name) {
      return "/resources/html/jsrendertemplates/" + name + ".tmpl.html";
}

function getProjetcName() {
	return getParameterByName('project');
}

function postToUrl(url, postData) {
	var form = document.createElement("form");
	form.setAttribute("method", "post");
	form.setAttribute("action", url);
	
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "data");
	hiddenField.setAttribute("value", JSON.stringify(postData));
	form.appendChild(hiddenField);
	
	document.body.appendChild(form);
	form.submit();
}

function toast(type, msg, autoClose) {
	if (autoClose === undefined)
		autoClose = true;
	$('.top-right').notify({
	    message: 
	    { 
	    	text: msg
	    },
		type: type,
		fadeOut: { enabled: autoClose}
	  }).show(); 
}

function getInputValues(container) {
	var parameters = {};
	$(container).find(':input').each( function() {
		if ((this.id !== '') && ($(this).val() !== '')) {
			if (this.type === 'checkbox') {
				parameters[this.id] = this.checked;
			} else  { 
				parameters[this.id] = $(this).val();
			}
		}
	});
	return parameters;
}

function displayAlert(txt, type) {
	if (type === undefined) {
		type = 'info';
	}
	$('#mainContainer').prepend('<div class="row"><div class="span12">' +
	    		'<div id="alertContainer" class="alert alert-' + type + ' fade in">' +
	            '<button type="button" class="close" data-dismiss="alert">&times;</button>'+ txt
	            	 + '</div></div>');
	
	$('#alertMessage').alert();
}

function dismissAlert() {
	$("#alertContainer").alert('close');
}

function getParameterByName(name) {
   name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
   var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
       results = regex.exec(location.search);
   return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
 
function renderTemplate(tmplName, targetSelector, data, callback) {
      var file = formatTemplatePath(tmplName);
      $.get(file, null, function (template) {
        var tmpl = $.templates(template);
        var htmlString = tmpl.render(data);
        if (targetSelector) {
          $(targetSelector).html(htmlString);
        }
        if (callback)
          callback();
        return htmlString;
      });
}


$("#projectSearch").click( function() {
	  var val = $("#searchValue").val();
	  console.log("Will search for " + val);
	  location.href = "/?projectSearch=" +  val;
}); 



function escapeSelector(selector) {
    return selector.replace(/(!|"|#|\$|%|\'|\(|\)|\*|\+|\,|\.|\/|\:|\;|\?|@)/g, function($1, $2) {
        return "\\" + $2;
    });
}

function switchVersion(node, link, projectName)
{
  var versionOrLabel = document.getElementById(node+'_commandParams').value;
  var version = getVersionForLabel(versionOrLabel, document.getElementById('projectName').value);
  commandWithModal('switch version', 'switching node ' + node + ' to version ' + version + ' (' + versionOrLabel + ')',
      function(){window.location =  '/command-to-node?command=switch-version&project='+encodeURIComponent(projectName)+'&link=' + encodeURIComponent(link + encodeURIComponent(version));});
}

function commandWithModal(title, content, func)
{
  $( "#dialog-confirm" ).attr('title', title);
  $( "#dialog-confirm" ).html("<p><span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span>" + content + ". Are you sure?</p>");
  $(function() {
    $( "#dialog-confirm" ).dialog({
      resizable: true,
      modal: true,
      buttons: {
        'Execute': function() {
          $( this ).dialog( "close" );
          func();
        },
        Cancel: function() {
          $( this ).dialog( "close" );
        }
      }
    });
  });
}

function commandNode(node, command, link, projectName)
{
	var params = document.getElementById(node+'_commandParams').value;
	commandWithModal('command node', 'sending command \'' + command + '\' to node ' + node + ' with parameters \'' + params + '\'', 
      function(){window.location =  '/command-to-node?command='+command+'&project='+encodeURIComponent(projectName)+'&link=' + encodeURIComponent(link + encodeURIComponent(params));});
}
function getVersionForLabel(label, projectName)
{
  var result1;
  var p = encodeURIComponent(projectName);
  var l = encodeURIComponent(label);
  jQuery.ajax({
    url:    '/label-version?project='+p+'&label='+l,
    success: function(result) {
      result1 = result;
             },
    async:   false
  }); 
  return result1;
}
function getCheckedNodes()
{
  var dateRE = /^checkbox_/;
  var arr=[],els=document.getElementsByTagName('*');
  for (var i=els.length;i--;) 
  {
    if (dateRE.test(els[i].id)) 
    {
      arr.push(els[i]);
    }
  }
  var length = arr.length;
  var nodes = "";
  for (var i = 0; i < length; i++) 
  {
    if (arr[i].checked)
    {
      nodes += arr[i].id.substring("checkbox_".length) + ",";
    }
  }
  return nodes;
}
function switchVersionToCheckedItems(project)
{
	var nodes = getCheckedNodes();
	var versionOrLabel = document.getElementById('commandAllArgs').value;
	var version = getVersionForLabel(versionOrLabel, document.getElementById('projectName').value);
	commandWithModal('switch version', 'switching to version ' + version + ' (' + versionOrLabel + ') nodes ' + nodes, 
      function(){commandCheckedItemsInternal(project, "switch-version", version, nodes);});
}
function commandCheckedItems(project, command)
{
  var nodes = getCheckedNodes();
  var userArgs = document.getElementById('commandAllArgs').value;
  commandWithModal('executing "' + command +'"', 'executing "' + command + '" with arguments: "' + userArgs + '" on nodes: ' + nodes, 
      function(){commandCheckedItemsInternal(project, command, userArgs, nodes);});
}
function commandCheckedItemsInternal(project, command, userArgs, nodes)
{
  var parametrs = {};
  parametrs["user_args"] = userArgs;
  parametrs["nodes"] = nodes;
  parametrs["command"] = command;
  parametrs["project"] = project;
  post_to_url("/command-node-all", parametrs);
}
function post_to_url(path, params, method) {
  method = method || "post"; // Set method to post by default if not specified.

  // The rest of this code assumes you are not using a library.
  // It can be made less wordy if you use one.
  var form = document.createElement("form");
  form.setAttribute("method", method);
  form.setAttribute("action", path);

  for(var key in params) {
      if(params.hasOwnProperty(key)) {
          var hiddenField = document.createElement("input");
          hiddenField.setAttribute("type", "hidden");
          hiddenField.setAttribute("name", key);
          hiddenField.setAttribute("value", params[key]);

          form.appendChild(hiddenField);
       }
  }

  document.body.appendChild(form);
  form.submit();
}
function selectAll()
{
	var dateRE = /^checkbox_/;
	var arr=[],els=document.getElementsByTagName('*');
	for (var i=els.length;i--;) 
	{
		if (dateRE.test(els[i].id)) 
		{
			arr.push(els[i]);
		}
	}
	var length = arr.length;
	var nodes = "";
	for (var i = 0; i < length; i++) 
	{
		arr[i].checked = true;
	}
}

function viewDashboard(version, projectParams, alerts)
{
	var versionCount = document.getElementById(version+'_input').value;
	var regexp = document.getElementById(version+'_regexp_input').value;
	window.location =  "/dashboard?version=" + encodeURIComponent(version) + "&count=" + encodeURIComponent(versionCount) + "&regexp=" + encodeURIComponent(regexp) + "&alerts=" + alerts + projectParams;
}
function updateLabel(projectName)
{
	var label = document.getElementById('add_label').value;
	var version = document.getElementById('add_version').value;
	var description = document.getElementById('add_description').value;
	var url = "/labels?project=" + encodeURIComponent(projectName);
	var url2 = url;
	
  // collect the form data while iterating over the inputs
  var data = {};
  data['label'] = label;
  data['version'] = version;
  data['description'] = description;
  data['project'] = projectName;

  myPostJson(url, url2, data);
}  
function myPostJson(url, url2, data)
{
  // construct an HTTP request
  var xhr = new XMLHttpRequest();
  xhr.open('post', url, true);
  xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

  // send the collected data as JSON
  xhr.send(JSON.stringify(data));

  xhr.onloadend = function () {
    window.location =  url2;
  };
}
function deleteLabel(projectName, label)
{
	var url = "/labels?project=" + encodeURIComponent(projectName) + "&label=" + encodeURIComponent(label);
	var url2 = "/labels?project=" + encodeURIComponent(projectName);
	
  // collect the form data while iterating over the inputs
  var data = {};

  // construct an HTTP request
  var xhr = new XMLHttpRequest();
  xhr.open('delete', url, true);
  xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

  // send the collected data as JSON
  xhr.send(JSON.stringify(data));

  xhr.onloadend = function () {
    window.location =  url2;
  };
}
function postJson(url, json)
{
  myPostJson(url, url, json);
}

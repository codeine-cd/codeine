
function switchVersion(node,link)
{
	alert('switching node ' + node + ' to version ' + document.getElementById(node+'_newVersion').value);
	window.location =  link + document.getElementById(node+'_newVersion').value;
}

function commandNode(node, command, link)
{
	alert('sending command \'' + command + '\' to node ' + node);
	window.location =  link;
}

function switchVersionToCheckedItems()
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
	for (var i = 0; i < length; i++) {
		arr[i].checked;
		nodes += arr[i].id + ",";
	}
	alert('switching nodes ' + nodes);
	window.location =  "/switchVersionAll?ndoes=" + nodes;
}

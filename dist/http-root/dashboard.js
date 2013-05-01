
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
	//checkbox_
	alert('switching node ');
}
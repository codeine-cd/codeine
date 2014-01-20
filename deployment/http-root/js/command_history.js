var commandsHistoryJson = undefined;

$(document).ready( function () {
	getCommandsHistory();
	setInterval(getCommandsHistory, 5000);
});




function getCommandsHistory() {
  $.ajax( {
    type: 'GET',
    url: '/commands-log_json?project=' + getProjetcName()  ,
    success: function(response) {
      if (commandsHistoryJson === undefined) {
    	  commandsHistoryJson = response;
      }
      if (response.length === 0) {
    	  $('#command_history_list').html("<li class='text-center'>No Commands</li>");
    	  return;
      }
      renderTemplate('command_history', $("#command_history_list") , response, function() {
        $(".commandStatus").tooltip();
        $('.deleteCommand').click(function() {
        	if (confirm("Are you sure you would like to stop the command?") === false) 
        		return;
    		$.ajax( {
    		    type: 'DELETE',
    		    url: '/command-nodes_json?project=' + $(this).data('project') + '&command-id=' + $(this).data('id')  ,
    		    success: function(resposne) {
    		    	console.log("success");
    		    	displayAlert('Command was canceled','success');
    		    	getCommandsHistory();
    		    }, 
    		    error: function(error) {
    		    	console.log("error");
    		    	displayAlert('Failed to cancel command','error');
    		    }
    		});
    	});
      });
    },
    error:  function(err) { 
      console.log('error is ' + err);
      },
    dataType: 'json'
  });
      
}


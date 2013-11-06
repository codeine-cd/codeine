$(document).ready( function () {
	console.log("READY getCommandsHistory");
	getCommandsHistory();
	setInterval(getCommandsHistory, 5000);
});




function getCommandsHistory() {
  $.ajax( {
    type: 'GET',
    url: '/commands-log?project=' + getProjetcName()  ,
    success: function(response) {
      console.log('got ' + response.length + " commands")
      renderTemplate('command_history', $("#command_history_list") , response, function() {
        $(".commandStatus").tooltip();
        $('.deleteCommand').click(function() {
    		$.ajax( {
    		    type: 'DELETE',
    		    url: '/command-nodes?project=' + $(this).data('project') + '&command-id=' + $(this).data('id')  ,
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


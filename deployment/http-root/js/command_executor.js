$(document).ready( function () {
	getCommandsStatus();
	setInterval(getCommandsStatus, 5000);
});

function getCommandsStatus() {
  $.ajax( {
    type: 'GET',
    url: '/commands-status_json',
    success: function(response) {
    	if (response.length === 0) {
      	  $('#command_executer_list').html("<li class='text-center'>No running ommands</li>");
      	  return;
        }
    	renderTemplate('command_executor', $("#command_executer_list") , response, function() {
    		$(".commandStatus").tooltip();
	        $('.deleteCommand').click(function() {
	        	if (confirm("Are you sure you would like to stop the command?") === false) 
	        		return;
	    		$.ajax( {
	    		    type: 'DELETE',
	    		    url: '/command-nodes_json?project=' + encodeURIComponent($(this).data('project')) + '&command-id=' + $(this).data('id')  ,
	    		    success: function(resposne) {
	    		    	console.log("success");
	    		    	displayAlert('Command was canceled','success');
	    		    	getCommandsStatus();
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
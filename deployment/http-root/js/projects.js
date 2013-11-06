

$(document).ready( function () {
	console.log("READY");
	var searchTerm = getParameterByName("projectSearch");
	if (searchTerm !== '') {
	  	$("#searchTerm").html(searchTerm);
	  	$("#searchHeader").show();
	}
	getCommandsStatus();
	setInterval(getCommandsStatus, 5000);
});

function getCommandsStatus() {
  $.ajax( {
    type: 'GET',
    url: '/commands-status',
    success: function(response) {
      console.log('got ' + response.length + " commands")
      renderTemplate('command_executor', $("#command_executer_list") , response, function() {
        $(".commandStatus").tooltip();
        $('.deleteCommand').click(function() {
    		$.ajax( {
    		    type: 'DELETE',
    		    url: '/command-nodes?project=' + $(this).data('project') + '&command-id=' + $(this).data('id')  ,
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
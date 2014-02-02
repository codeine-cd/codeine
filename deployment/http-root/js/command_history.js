var commandsHistoryJson = undefined;
var historyMore = false;
var allHistory = [];
var maxHistorySizeToShow = 10;

$(document).ready( function () {
	getCommandsHistory();
	setInterval(getCommandsHistory, 5000);
});


$("#more_history_button").click(function(){
	historyMore = true;
	renderHistory(allHistory);
	$("#command_history_more").fadeOut();
});

function renderHistory(historyArray){
	renderTemplate('command_history', $("#command_history_list") , historyArray, function() {
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
}
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
      allHistory = response;
      var historyArray = response;
      if (response.length > maxHistorySizeToShow && !historyMore) {
    	  historyArray = response.slice(0,maxHistorySizeToShow);
    	  $("#command_history_more").show();
      } else {
    	  $("#command_history_more").hide();
      }
      renderHistory(historyArray);
    },
    error:  function(err) { 
      console.log('error is ' + err);
      },
    dataType: 'json'
  });
      
}


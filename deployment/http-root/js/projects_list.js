

$(document).ready( function () {
	if (isAdmin) {
	  $('.admin').show();
	} else {
	  $('.admin').hide();
	}
	var searchTerm = getParameterByName("projectSearch");
	if (searchTerm !== '') {
	  	$("#searchTerm").html(searchTerm);
	  	$("#searchHeader").show();
	  	$("#myTab").hide();
	}
	if (($('.codeine_project').length === 0) && (isUserLogged()) && (isAdmin)) {
		displayAlert("No configured projects, press <a href='/new-project'>'New Project'</a> to create one.", "warning");
	}
});


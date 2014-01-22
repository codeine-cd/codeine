
$(document)
		.ready(
				function() {
					if (isAdmin) {
						$('.admin').show();
						checkForNewCodeine();
					} else {
						$('.admin').hide();
					}
					var searchTerm = getParameterByName("projectSearch");
					if (searchTerm !== '') {
						$("#searchTerm").html(searchTerm);
						$("#searchHeader").show();
						$("#myTab").hide();
						if ($(".codeine_project").length === 0) {
							$("#myTabContent")
									.html(
											"<div class='row'><div class='span9 text-center'><h2 class='center'>- No Projects found -</h2></div></div>");
						}
					} else if (($('.codeine_project').length === 0)
							&& (isUserLogged() || authentication_method === 'Disabled')
							&& (isAdmin)) {
						displayAlert(
								"No configured projects, press <a href='/new-project'>'New Project'</a> to create one.",
								"warning");
						if ($(".codeine_project").length === 0) {
							$("#myTabContent")
									.html(
											"<div class='row'><div class='span9 text-center'><h2 class='center'>- No Projects -</h2></div></div>");
						}
					}

				});

function getLatestAndGreatest(releases) {
	for (var i = 0; i < releases.length; i++) {
		if (releases[i]["prerelease"] === false) {
			return releases[i];
		}
	}
	return releases[0];
}
function checkForNewCodeine() {
	console.log("Will look for new Codeine version in Github");
	$
			.ajax({
				type : 'GET',
				url : 'https://api.github.com/repos/Intel-IT/codeine/releases',
				success : function(releases) {
					var release = getLatestAndGreatest(releases);
					var verString = release["name"];
					var latest_version = parseInt(release["name"]
							.substring(release["name"].lastIndexOf(".") + 1));
					var current_version = parseInt($('#codeine_version').html()
							.substring(
									$('#codeine_version').html().lastIndexOf(
											".") + 1));
					console.log("Current Version: " + current_version
							+ " Latest Version: " + latest_version);
					if (latest_version > current_version) {
						displayAlert(
								"New Codeine Version ("
										+ verString
										+ ") is avaliable on <a href='https://github.com/Intel-IT/codeine/releases/latest'><i class='fa fa-github'></i> GitHub</a>, click <a href='/upgrade-server?version="
										+ encodeURIComponent(verString)
										+ "'>here</a> to upgrade now", "info");
					}
				},
				error : function(jqXhr) {
					console
							.warn('Failed to get releases from Github: '
									+ jqXhr);
				},
				dataType : 'json'
			});
}

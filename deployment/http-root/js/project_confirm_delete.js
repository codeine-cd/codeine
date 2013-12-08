$(document).ready( function () {
	
	$("#delete_button").click(function() {
    console.log("delete project " + getProjetcName());
    $.ajax(
        {
            type: 'DELETE',
            url: '/delete-project?project=' + encodeURIComponent(getProjetcName()),
            success: function () {
              console.log("project deleted");
              window.location = "/";
            },
            error: function (jqXhr) {
              toast("danger", "Failed to delete project: " + jqXhr.responseText,false);
            },
        }
    );
  });
});


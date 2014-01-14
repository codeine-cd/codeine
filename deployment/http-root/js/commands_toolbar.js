$(document).ready( function () {
  $('.dropdown-toggle').dropdown();
  
  
  
  $('a').click(function(e) {
    if ($(this).parent().hasClass("disabled")) {
      e.stopPropagation();
      e.preventDefault();
    }
  });
  

});

$('#selectAll').change(function() {
  var value = $(this).is(":checked");
  $('.panel-body').find('[type=checkbox]:visible').prop('checked', value);
  if (value === true) {
    $('.codeine_command').removeClass("disabled");
  }
});


$('[type=checkbox]').click( function() {
  resetSelectAll();
});

function allChecked() {
	$('.panel-body').find('[type=checkbox]:visible').each(function(){
		  if ($(this).prop('checked') != true){
			  return false;
		  }
	  });
	return true;
}
function hasChecked() {
	$('.panel-body').find('[type=checkbox]:visible').each(function(){
		if ($(this).prop('checked') == true){
			return true;
		}
	});
	return false;
}
function resetSelectAll() {
  var allChecked = allChecked();
  var hasChecked = hasChecked();
  $("#selectAll").prop("checked", allChecked);
  if (hasChecked) {
    $('.codeine_command').removeClass("disabled");
  } else {
    $('.codeine_command').addClass("disabled");
  }
}
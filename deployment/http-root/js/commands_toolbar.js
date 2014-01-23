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
    $('.codeine_command').prop('disabled', false);
  }
});


$('[type=checkbox]').click( function() {
  resetSelectAll();
});

function allComboChecked() {
	var allChecked = true;
	$('.panel-body').find('[type=checkbox]:visible').each(function(){
		  if ($(this).prop('checked') != true){
			  allChecked = false;
			  return false;
		  }
	  });
	return allChecked;
}
function hasComboChecked() {
	var hasChecked = false;
	$('.panel-body').find('[type=checkbox]:visible').each(function(){
		if ($(this).prop('checked') == true){
			hasChecked = true;
			return false;
		}
	});
	return hasChecked;
}
function resetSelectAll() {
  var allChecked = allComboChecked();
  var hasChecked = hasComboChecked();
  $("#selectAll").prop("checked", allChecked);
  enableRunCommandButton(hasChecked);
}
function enableRunCommandButton(hasChecked) {
  if (hasChecked && (typeof command === 'undefined' || command !== "'command not selected'")) {
    $('.codeine_command').removeClass("disabled");
    $('.codeine_command').prop('disabled', false);
  } else {
    $('.codeine_command').addClass("disabled");
    $('.codeine_command').prop('disabled', true);
  }
}

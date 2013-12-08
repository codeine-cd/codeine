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

function resetSelectAll() {
  var allChecked = true;
  var hasChecked = false;
  var index = 1;
  var checkbox;
  while ((checkbox = $('#checkbox_' + index)).length > 0) {
    if (checkbox.is(':visible') && checkbox.is(':checked')) {
      hasChecked = true;
    }
    if (checkbox.is(':visible') && !checkbox.is(':checked')) {
      allChecked = false;
    }
    if (hasChecked && !allChecked)
    {
      break;
    }
    index++;
  }
  $("#selectAll").prop("checked", allChecked);
  if (hasChecked) {
    $('.codeine_command').removeClass("disabled");
  } else {
    $('.codeine_command').addClass("disabled");
  }
}
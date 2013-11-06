$(document).ready(function(){
	$(".chosen-select").chosen({disable_search_threshold: 10});
	$('.progressiveControl').addClass('hidden');
});


$('#commandStrategy').change(function() {
	setUI();
})

$('#submitCommand').click(function() {
	var parameters = getInputValues($('#commandData'));
	parameters["nodes"] = nodes;
	console.log('will submit command ' + parameters);
	postToUrl('/command-nodes',parameters);
});

$('#stopOnErrorCheckbox').change(function() {
	if ($('#errorPercent').hasClass('hidden')) {
		$('#errorPercent').removeClass('hidden');
	} else {
		$('#errorPercent').addClass('hidden');
	}
});

$(".accordion").on("show",function(event){
    collapse_element = event.target;
    console.log(collapse_element.id + " show");
    $(collapse_element).parent().find('.fa-plus-circle').removeClass('fa-plus-circle').addClass('fa-minus-circle')
});

$(".accordion").on("hide",function(event){
    collapse_element = event.target;
    console.log(collapse_element.id + " hide");
    $(collapse_element).parent().find('.fa-minus-circle').removeClass('fa-minus-circle').addClass('fa-plus-circle')
    
});

function setUI() {
	var value = $('#commandStrategy').find('option:selected').val();
	console.log('current duration ' + value);
	if (value.toLowerCase() === 'progressive') {
		$('.progressiveControl').removeClass('hidden').fadeIn('slow');
		$('.immediatelyControl').fadeOut('slow');
	} else {
		$('.progressiveControl').fadeOut('slow');
		$('.immediatelyControl').fadeIn('slow');
	} 
}

var chart1;
var chartDetails;


$(document).ready( function () {
    loadChart();
});

function loadChart() {
	$.ajax( {
	    type: 'GET',
	    url: '/monitors-statistics_json?project=' + getProjetcName(),
	    beforeSend: function() {
	    	console.log("Sending chart data request...");
	    	// TODO - Show loader...
	    },
	    success: function(response) {
	    	// TODO - Hide loader...
	    	chart1 = new cfx.Chart();
	    	chart1.setDataSource(jQuery.parseJSON(response));
	    	chart1.getAllSeries().setMarkerShape(cfx.MarkerShape.None);
	    	chart1.getAllSeries().getLine().setStyle(cfx.DashStyle.Dash);

	    	var chartDiv = document.getElementById('chartDiv');
	    	chart1.create(chartDiv);

		    // Change the chart's attributes to make it responsive.
    		var svgelem = document.getElementsByTagName("svg");
			svgelem.chart.setAttribute('width','100%');
			// svgelem.chart.setAttribute('height',''); 			// Removing height makes the chart disappear in Chrome :(
			svgelem.chart.setAttribute('viewBox','0 0 870 600');
	    },
	    error:  function(err) { 
	    	// TODO - Hide loader...
	    	console.log('error is ' + err);
	    },
	    dataType: 'json'
	  });
}
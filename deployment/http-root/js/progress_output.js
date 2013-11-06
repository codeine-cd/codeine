
var lines = 0;
var interval;

$(document).ready( function () {
	addLines();
	interval =  setInterval(addLines, 5000);
});

function addLines() {
  console.log("addLines");
  $.get("/file-getter?project=" + projectName + "&path=" + path + "&line=" + lines, function( data ) {
    var obj = JSON.parse(data);
    $("#output_pre" ).append( obj.lines.join("\n") );
    lines += obj.lines.length;
    if (obj.eof) {
      clearInterval(interval);
      $('#output_spinner').fadeOut();
    }
  }); 
     
}

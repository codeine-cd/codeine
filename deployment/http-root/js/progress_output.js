var lines = 0;
var interval;

$(document).ready(function() {
	addLines();
	interval = setInterval(addLines, 5000);
});

function addLines() {
	console.log("addLines");
	$.get("/file-getter?project=" + encodeURIComponent(projectName) + "&path="
			+ encodeURIComponent(path) + "&line=" + encodeURIComponent(lines),
			function(data) {
				var scrolledToBottom = $(window).scrollTop() + $(window).height() > $(document).height() - 100;
				var obj = JSON.parse(data);
				$("#output_pre").append(obj.lines.join("\n"));
				if (obj.lines.length > 0) {
					$("#output_pre").append("\n");
				}
				lines += obj.lines.length;
				if (scrolledToBottom) {
					$(document).scrollTop($(document).height());
				}
				if (obj.eof) {
					clearInterval(interval);
					$('#output_spinner').fadeOut();
				}
			});

}

$(document).ready(function() {

	/** Loading topHeader html template and the script * */
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab1").addClass("tab-selected");

	})

	$("#favored-tags").tagit();
	$("#competences-tags").tagit({
		readOnly : true
	});

});

// COOKIE DATA --
var key = $.cookie('key');
var last_name = $.cookie('lastname');
var first_name = $.cookie('firstname');


if (key != 'null' && key != null)
	window.location.href = "index";


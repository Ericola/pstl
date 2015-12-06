/*********************************************************/
/******					GLobal variables		**********/
/** ****************************************************** */
var token = $.cookie(Cookie.TOKEN);


/** ****************************************************** */
/** **** Document ready ********* */
/** ****************************************************** */
$(document).ready(function() {

	// Loading top header
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab1").addClass("tab-selected");
	})

	// Event listeners
	$("#offers-container").on("click", ".btn-detail", function(event) {
		offer = $(this).parent().parent().parent();
		window.location.href = "offer.html?offer_id=" + offer.attr("offer_id");
	})

	// Ajax call get all offers
	getOffers();

});

function onSuccess(tab) {
	for (i = 0; i < tab.length; i++) {
		$("#offers-container").loadTemplate("/pstl/templates/offer.html", tab[i], {
			append : true,
		});
	}
}

function getOffers() {
	$.ajax({
		type : "GET",
		url : "get_all_offer.json",
		dataType : "json",
		data : {
			// id : id
			// number : nb_posts,
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code)
				notifyError("Un probleme est survenu :(");
			else {
				//				var text = data.description;
				//				test = text.
				//				var text = data.description.match(/\(?[A-Z][^\.]+[\.!\?]\)?(\s+|$)/g);
				//				data.description = text[0];
				onSuccess(data);
				// lastPost = data[data.length - 1];

			}

		},
		error : function(data) {
			notifyError("error" + data);
		},
	});
}

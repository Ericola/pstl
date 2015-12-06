// Offres d'emplois
var tab;

// Infos utilisateur
var token = $.cookie(Cookie.TOKEN);

//
$(document).ready(function() {

	/** Loading topHeader html template and the script * */
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab3").addClass("tab-selected");

	})

	tab = [ {
		title : "Développeur Informatique",
		location : "Bayonne",
		published_date : "27 Février 2020",
		description : "Ceci est une description de cette offre tres intéressante pour vous..."
	}, {
		title : "Développeur Informatique",
		location : "Lyon",
		published_date : "27 Février 2020",
		description : "Ceci est une description de cette offre tres intéressante pour vous..."
	}, {
		title : "Développeur Informatique",
		location : "Lyon",
		published_date : "27 Février 2020",
		description : "Ceci est une description de cette offre tres intéressante pour vous..."
	} ];

	$("#offers-container").on("click", ".btn-detail", function(event) {
		offer = $(this).parent().parent().parent();
		window.location.href = "offer.html?offer_id=" + offer.attr("offer_id")	;
	})

	$.ajax({
		type : "GET",
		url : "get_potential_offers.json",
		dataType : "json",
		data : {
			token : token
		},
		success : function(data) {
			if (!(data.error_code == ErrorCodes.SESSION_EXPIRED)) {

				for (i = 0; i < data.length; i++) {
					$("#offers-container").loadTemplate("/pstl/templates/potential-offer.html", data[i], {
						append : true
					});
				}

			} else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});

});

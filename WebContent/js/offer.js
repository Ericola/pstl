var token = $.cookie(Cookie.TOKEN);

$(document)
.ready(
		function() {
			offer_id = getUrlParameter('offer_id');
			/** Loading topHeader html template and the script * */
			$("#top-header").load("top_header.html", function() {
				$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
				$("#tab1").addClass("tab-selected");

			})

			$("#competences-requises-tags").tagit({
				readOnly : true
			});
//			$("#description")
//			.text(
//			"L'employeur décrit ici la mission. \nLe contenu de la description sera indexée. \nLe moteur de recherche se basera sur la descprition pour trouver les offres pertinentes\n")
//			$("#competences")
//			.text(
//			"- L'employeur décrit ici les compétences requises pour la mission.\n - Les compétences seront indéxées. \n - Le moteur de recherche se basera sur la descprition pour trouver les offres pertinentes")

//			$("#contact").text("- 06 06 06 06 14\n - vincent@tingting.com")
			getSpecOffer();

		});

/*******************************************************************************
 * AJAX CALLS *
 ******************************************************************************/

function getSpecOffer() {
	var id = offer_id
	$.ajax({
		type : "GET",
		url : "get_spec_offer.json",
		dataType : "json",
		data : {
			id : id
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code)
				notifyError("Un probleme est survenu :(");
			else {
				$(".offer-title").text(data.title);
				$("#location").text(data.location);
				$("#date").text(data.published_date);
				$("#start-date").text(data.start_date);
				$("#duration").text(data.mission_duration);
				$("#business").text(data.business);
				$("#description").text(data.description);
				var requireCompetence = data.required_competence;
				for (var i = 0; i < requireCompetence.length; i++) {
					$("#competences-requises-tags").tagit("createTag", requireCompetence[i]);
				}
				$("#contact").text(data.contact);
			}

		},
		error : function(data) {
			notifyError("error" + data);
		},
	});
}
/*********************************************************/
/******					Global variables		**********/
/** ****************************************************** */

var token = $.cookie(Cookie.TOKEN);
var isEditingOfferHeader = false;
var isEditingOfferDescription = false;
var isEditingOfferCompetence = false;
var isEditingOfferContact = false;

var offer = {
	"published_date" : "",
	"title" : "",
	"location" : "",
	"required_competence" : [],
	"description" : "",
	"mission_duration" : "",
	"contact" : "",
	"start_date" : "",
	"business" : ""
};

var requireCompetence = [ "" ];
var availableCompetences = [];
var section = 7;
$(document).ready(function() {
	offer_id = getUrlParameter('offer_id');
	$("#datepicker").datepicker($.datepicker.regional["fr"]);
	/** Loading topHeader html template and the script * */
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab1").addClass("tab-selected");

	});

	$("#datepicker").datepicker();
	$(".offer-overlay").click(hideOfferEdit);
	$("#btn-edit-offer-header").click(function() {
		section = 0;
		if (isEditingOfferHeader) {
			$(".offer-header").show();
			$("#editing-offer-header").hide();

			$(".offer-overlay").hide();
			$('.section').removeAttr('style');
			resetEditingHeader();
		} else {
			$(".offer-header").hide();
			$("#editing-offer-header").show();

			$(".offer-overlay").show();
			$(".section#section-header").css({
				"z-index" : 999
			});
		}

		isEditingOfferHeader = !isEditingOfferHeader;
	});

	$("#btn-cancel-edit-offer-header").click(btnCancelEditOfferHeader);

	$("#btn-edit-offer-description").click(function() {
		section = 1;
		if (isEditingOfferDescription) {
			$("#description").show();
			$("#editing-offer-description").hide();

			$(".offer-overlay").hide();
			$('.section').removeAttr('style');

			$("#description-text-field").val(offer.description);

		} else {
			$("#description").hide();
			$("#editing-offer-description").show();

			$(".offer-overlay").show();
			$(".section#section-description").css({
				"z-index" : 999
			});
		}

		isEditingOfferDescription = !isEditingOfferDescription;
	});

	$("#btn-cancel-edit-offer-description").click(btnCancelEditOfferDescription);

	$("#competences-tags").tagit({
		readOnly : true
	});
	$("#competences-editing-tags").tagit({
		readOnly : false,
		availableTags : availableCompetences,
		autocomplete : {
			delay : 0,
			minLength : 2,
			source : getAvailableCompetences,

		}	
	});

	$("#btn-edit-offer-competences, #btn-cancel-edit-offer-competences").click(btnEditOfferCompetence);

	$("#btn-edit-offer-contact").click(function() {
		section = 3;
		if (isEditingOfferContact) {
			$("#contact").show();
			$("#editing-offer-contact").hide();

			$("#contact-text-field").val(offer.contact);

			$(".offer-overlay").hide();
			$('.section').removeAttr('style');
		} else {
			$("#contact").hide();
			$("#editing-offer-contact").show();

			$(".offer-overlay").show();
			$(".section#section-contact").css({
				"z-index" : 999
			});
		}

		isEditingOfferContact = !isEditingOfferContact;
	})

	$("#btn-cancel-edit-offer-contact").click(btnCancelEditOfferContact);

	$("#btn-validate-edit-description").click(updateDescription);

	$("#btn-validate-edit-info").click(updateInfo);

	$("#btn-validate-edit-offer-competences").click(updateCompetences);

	$("#btn-validate-edit-offer-contact").click(updateContact);
	if (undefined != offer_id)
		getSpecOffer();
});

/*******************************************************************************
 * AJAX CALLS *
 ******************************************************************************/
function getAvailableCompetences(request, response) {
	jQuery.getJSON("get_all_comp.json", {
		token : token,
		mot : request.term
	}, function(data) {
		response(data.competences);
	})
}

function updateDescription() {
	var column_id = 1;
	var description = $("#description-text-field").val();
	$.ajax({
		type : "GET",
		url : "create_offer.json",
		dataType : "json",
		data : {
			token : token,
			offer_id : offer_id,
			column_id : column_id,
			description : description

		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				if (!data.ok) { // Si l'offre n'existe pas deja
					offer_id = data.id;
				}

				offer.description = description;

				$(".offer-overlay").hide();
				$('.section').removeAttr('style');

				$("#description").text(description);
				$("#description").show();
				$("#editing-offer-description").hide();
				isEditingOfferDescription = false;
			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function updateInfo() {
	var column_id = 0;
	var title = $("#offer-title").val();
	var location = $("#location-offer").val();
	var testDate = /^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/;
	var start_date = $("#datepicker").val();
	var mission_duration = $("#mission-longer").val();
	var business = $("#business-activity").val();
	if (!testDate.test(start_date)) {
		alert("Date non valide !")
	}
	$.ajax({
		type : "GET",
		url : "create_offer.json",
		dataType : "json",
		data : {
			token : token,
			offer_id : offer_id,
			column_id : column_id,
			title : title,
			location : location,
			start_date : start_date,
			mission_duration : mission_duration,
			business : business
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				if (!data.ok) { // Si l'offre n'existe pas deja
					offer_id = data.id;
				}

				offer.title = title;
				offer.location = location;
				offer.start_date = start_date;
				offer.mission_duration = mission_duration;
				offer.business = business;
				$(".offer-overlay").hide();
				$('.section').removeAttr('style');

				$(".offer-title").text(title);
				$("#location").text(location);
				$("#start-date").text(start_date);
				$("#duration").text(mission_duration);
				$("#business").text(business);
				$(".offer-header").show();
				$("#editing-offer-header").hide();
				isEditingOfferHeader = false;
			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function updateCompetences() {
	var column_id = 2;
	requireCompetence = $("#competences-editing-tags").tagit("assignedTags");
	$.ajax({
		type : "GET",
		url : "create_offer.json",
		dataType : "json",
		data : {
			token : token,
			offer_id : offer_id,
			column_id : column_id,
			required_competence : requireCompetence
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				if (!data.ok) { // Si l'offre n'existe pas deja
					offer_id = data.id;
				}

				$(".offer-overlay").hide();
				$('.section').removeAttr('style');
				$("#competences-tags").tagit("removeAll");
				if (requireCompetence.length == 0) {
					$('#empty-competences').show();
					$("#competences-tags").hide();
				} else {
					$('#empty-competences').hide();
					$("#competences-tags").show();
					for (var i = 0; i < requireCompetence.length; i++) {
						$("#competences-tags").tagit("createTag", requireCompetence[i]);
						$("#competences-editing-tags").tagit("createTag", requireCompetence[i]);

					}
				}
				$("#competences").show();
				$("#competences-editing").hide();
				isEditingOfferCompetence = false;
			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function updateContact() {
	var column_id = 3;
	var contact = $("#contact-text-field").val();
	$.ajax({
		type : "GET",
		url : "create_offer.json",
		dataType : "json",
		data : {
			token : token,
			offer_id : offer_id,
			column_id : column_id,
			contact : contact
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				if (!data.ok) { // Si l'offre n'existe pas deja
					offer_id = data.id;
				}

				offer.contact = contact;
				$(".offer-overlay").hide();
				$('.section').removeAttr('style');

				$("#contact").text(contact);
				$("#contact").show();
				$("#editing-offer-contact").hide();
				isEditingOfferContact = false;

			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

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
			offer = data;
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code)
				notifyError("Un probleme est survenu :(");
			else {
				offer = data;
				$(".offer-title").text(data.title);
				$("#offer-title").val(data.title);

				$("#location").text(data.location);
				$("#location-offer").val(data.location);

				$("#date").text(data.published_date);

				$("#start-date").text(data.start_date);
				$("#datepicker").val(data.start_date);

				$("#duration").text(data.mission_duration);
				$("#mission-longer").val(data.mission_duration);

				$("#business").text(data.business);
				$("#business-activity").val(data.business);

				$("#description").text(data.description);
				$("#description-text-field").val(data.description);

				if (data.required_competence == null)
					requireCompetence = [ "" ];
				else
					requireCompetence = data.required_competence;
				if (requireCompetence.length == 0) {
					$('#empty-competences').show();
					$("#competences-tags").hide();
				} else {
					$('#empty-competences').hide();
					$("#competences-tags").show();
					for (var i = 0; i < requireCompetence.length; i++) {
						$("#competences-tags").tagit("createTag", requireCompetence[i]);
						$("#competences-editing-tags").tagit("createTag", requireCompetence[i]);
					}
				}

				$("#contact").text(data.contact);
				$("#contact-text-field").text(data.contact);
			}

		},
		error : function(data) {
			notifyError("error" + data);
		},
	});

}

function resetEditingHeader() {

	$("#offer-title").val(offer.title);

	$("#location-offer").val(offer.location);

	$("#datepicker").val(offer.start_date);

	$("#mission-longer").val(offer.mission_duration);

	$("#business-activity").val(offer.business);

}
function btnCancelEditOfferHeader() {
	resetEditingHeader();

	$(".offer-header").show();
	$("#editing-offer-header").hide();

	$(".offer-overlay").hide();
	$('.section').removeAttr('style');
	isEditingOfferHeader = false;

}

function btnCancelEditOfferDescription() {
	$("#description").show();
	$("#editing-offer-description").hide();

	$("#description-text-field").val(offer.description);

	$(".offer-overlay").hide();
	$('.section').removeAttr('style');
	isEditingOfferDescription = false;
}

function btnCancelEditOfferContact() {
	$("#contact").show();
	$("#editing-offer-contact").hide();
	$("#contact-text-field").val(offer.contact);

	$(".offer-overlay").hide();
	$('.section').removeAttr('style');
	isEditingOfferContact = false;

}

function btnEditOfferCompetence() {
	section = 2;

	if (isEditingOfferCompetence) {
		$("#competences").show();
		$("#competences-editing").hide();
		$(".offer-overlay").hide();
		$('.section').removeAttr('style');

		if (requireCompetence.length == 0) {
			$('#empty-competences').show();
			$("#competences-tags").hide();
		} else {
			$('#empty-competences').hide();
			$("#competences-tags").show();
		}

		// on reset le editing tags avec les requirecompetence
		$("#competences-editing-tags").tagit("removeAll");
		for (var i = 0; i < requireCompetence.length; i++) {
			$("#competences-editing-tags").tagit("createTag", requireCompetence[i]);
		}
	} else {
		$("#competences").hide();
		$("#competences-editing").show();

		$(".offer-overlay").show();
		$(".section#section-competences").css({
			"z-index" : 999
		});
	}

	isEditingOfferCompetence = !isEditingOfferCompetence;
}
function hideOfferEdit() {

	switch (section) {
	case 0:
		btnCancelEditOfferHeader();
		break;
	case 1:
		btnCancelEditOfferDescription();
		break;
	case 2:
		btnEditOfferCompetence();

		break;
	case 3:
		btnCancelEditOfferContact();
		break;
	}
}

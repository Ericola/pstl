/*********************************************************/
/******					GLobal variables		**********/
/** ****************************************************** */
var isEditingFavored = false;
var isEditingExperience = false;
var isEditingCompetence = false;
var isEditingFormation = false;
var isEditingLanguage = false;

var favoredCompetenceEmpty = true;
var competenceEmpty = true;

var emptyMessage = "Cliquez sur le stylo pour ajouter du contenu";

// CV data
var favoredKeyword = [];// "web", "applications mobiles"
var competences = [];
var favoredCompetences = []
var experiences = [];
var formations = [];
var languages = [];

var token = $.cookie(Cookie.TOKEN);
var section = 7;

var availableCompetences = [];
/*******************************************************************************
 * /** * Document ready * /**
 ******************************************************************************/
$(document).ready(function() {

	$("#startDateFormation").datepicker($.datepicker.regional["fr"]);
	$("#endDateFormation").datepicker($.datepicker.regional["fr"]);
	$("#startDateExp").datepicker($.datepicker.regional["fr"]);
	$("#endDateExp").datepicker($.datepicker.regional["fr"]);

	/** Loading topHeader html template and the script * */
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab1").addClass("tab-selected");

	})

	$("#favored-tags").tagit({
		readOnly : true
	});

	$("#competences-tags").tagit({
		readOnly : true
	});

	$("#favored-tags-editing").tagit({
		autocomplete : {
			delay : 0,
			minLength : 2,
			source : getAvailableCompetences,
		}

	});
	$("#competences-tags-editing").tagit({
		availableTags : availableCompetences,
		autocomplete : {
			delay : 0,
			minLength : 2,
			source : getAvailableCompetences,

		}
	});

	$("#favored-tags").hide();
	$('#empty-favored').show();

	$("#competences-tags").hide();
	$('#empty-competences').show();

	/***********************************************************
	 * EVENT LISTENERS *
	 **********************************************************/
	$(".cv-overlay").click(hideCvEdit);
	$("#btn-edit-favored, #btn-cancel-edit-favored").click(btnEditFavoredAction);
	$("#btn-edit-experience, .btn-add-experience").click(btnEditExperienceAction);
	$("#btn-cancel-edit-experience").click(btnCancelEditExperienceAction);
	$("#btn-edit-competences, #btn-cancel-edit-competences").click(btnEditCompetencesAction);
	$("#btn-edit-formation, .btn-add-formation").click(btnEditFormationAction);
	$("#btn-cancel-edit-formation").click(btnCancelEditFormationAction);
	$("#btn-edit-language, .btn-add-language").click(btnEditLanguageAction);
	$("#btn-cancel-edit-language").click(btnCancelEditLanguageAction);
	$("#btn-validate-edit-favored").click(updateFavoredKeywords);
	$("#btn-validate-edit-competences").click(updateCompetence);
	$("#btn-validate-edit-formation").click(updateFormation);
	$("#btn-validate-edit-experience").click(addExperience);
	$("#btn-validate-edit-language").click(addLanguage);
	// empty favored
	// empty experience
	// cométences
	// formation
	// empty langues

	// if(favoredKeyword.length == 0)

	getCV();

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
function updateFavoredKeywords() {
	favoredCompetences = $("#favored-tags-editing").tagit("assignedTags");
	$.ajax({
		type : "GET",
		url : "update_favored_keywords.json",
		dataType : "json",
		data : {
			token : token,
			competences : favoredCompetences
		},
		success : function(data) {
			if (data.ok) {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				isEditingFavored = false;
				hideFavoredEditCompetences();

				$("#favored-tags").tagit("removeAll");
				for (var i = 0; i < favoredCompetences.length; i++)
					$("#favored-tags").tagit("createTag", favoredCompetences[i]);

			} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function addExperience() {
	var title = $("#experience-title").val();
	var company = $("#experience-company").val();
	var testDate = /^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/;
	var startDate = $("#startDateExp").val();
	var endDate = $("#endDateExp").val();
	var description = $("#descExp").val();
	var experience = {
		title : title,
		company : company,
		startDate : startDate,
		endDate : endDate,
		description : description
	};
	if (!testDate.test(startDate) || !testDate.test(endDate)) {
		alert("Date non valide !")
	}
	$.ajax({
		type : "GET",
		url : "add_experience.json",
		dataType : "json",
		data : {
			token : token,
			title : title,
			company : company,
			startDate : startDate,
			endDate : endDate,
			description : description
		},
		success : function(data) {
			if (data.ok) {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				$("#experience-container").loadTemplate("/pstl/templates/experience.html", experience, {
					append : true
				});
				btnCancelEditExperienceAction();
			} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function updateCompetence() {
	competences = $("#competences-tags-editing").tagit("assignedTags");
	$.ajax({
		type : "GET",
		url : "update_competences.json",
		dataType : "json",
		data : {
			token : token,
			competences : competences
		},
		success : function(data) {
			if (data.ok) {
				isEditingCompetence = false;
				hideEditingCompetences();

				$("#competences-tags").tagit("removeAll");
				for (var i = 0; i < competences.length; i++)
					$("#competences-tags").tagit("createTag", competences[i]);

			} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function updateFormation() {
	var school = $("#formation-location").val();
	var diploma = $("#formation-diploma").val();
	var testDate = /^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/;
	var startDate = $("#startDateFormation").val();
	var endDate = $("#endDateFormation").val();

	var formation = {
		school : school,
		diploma : diploma,
		startDate : startDate,
		endDate : endDate
	}
	if (!testDate.test(startDate) || !testDate.test(endDate)) {
		alert("Date non valide !")
	} else {
		$.ajax({
			type : "GET",
			url : "update_formation.json",
			dataType : "json",
			data : {
				token : token,
				diploma : diploma,
				school : school,
				startDate : startDate,
				endDate : endDate
			},
			success : function(data) {
				if (data.ok) {
					notify("Les modifications ont été enregistrées", Notif.DELAY);

					$("#formation-container").loadTemplate("/pstl/templates/formation.html", formation, {
						append : true
					});
					btnCancelEditFormationAction();

				} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
					logout();
				else
					notifyError("Un probleme est survenu :(");
			},
			error : function(data) {
				notifyError("error" + data);
			},

		});
	}
}

function addLanguage() {
	language = $("#language-name").val();
	level = $("#language-level").val();

	lang = {
		name : language,
		level : $("#language-level option:selected").text()
	};
	$.ajax({
		type : "GET",
		url : "add_language.json",
		dataType : "json",
		data : {
			token : token,
			language : language,
			level : level
		},
		success : function(data) {
			if (data.ok) {
				notify("Les modifications ont été enregistrées", Notif.DELAY);
				$(".langue-container").loadTemplate("/pstl/templates/language.html", lang, {
					append : true
				});
				btnCancelEditLanguageAction();
			} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

function getCV() {
	$.ajax({
		type : "GET",
		url : "get_cv.json",
		dataType : "json",
		data : {
			token : token
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else {
				if (data.favored_competences != null)
					favoredCompetences = data.favored_competences;
				if (favoredCompetences.length == 0) {
					$('#empty-favored').show();
					$("#favored-tags").hide();
				} else {
					$('#empty-favored').hide();
					$("#favored-tags").show();
					for (var i = 0; i < favoredCompetences.length; i++) {
						$("#favored-tags").tagit("createTag", favoredCompetences[i]);
					}
				}
				if (data.experience != null)
					experiences = data.experience;
				for (var i = 0; i < experiences.length; i++) {
					$("#experience-container").loadTemplate("/pstl/templates/experience.html", experiences[i], {
						append : true
					});
				}
				if (data.competences != null)
					competences = data.competences;
				if (competences.length == 0) {
					$('#empty-competences').show();
					$("#competences-tags").hide();
				} else {
					$('#empty-competences').hide();
					$("#competences-tags").show();
					for (var i = 0; i < competences.length; i++) {
						$("#competences-tags").tagit("createTag", competences[i]);
					}
				}
				if (data.formation != null)
					formations = data.formation;
				for (var i = 0; i < formations.length; i++) {
					$("#formation-container").loadTemplate("/pstl/templates/formation.html", formations[i], {
						append : true
					});
				}

				if (data.language != null)
					languages = data.language;
				for (var i = 0; i < languages.length; i++) {
					languages[i].level = levelLangugeToValue(languages[i].level);
					$(".langue-container").loadTemplate("/pstl/templates/language.html", languages[i], {
						append : true
					});
				}

			}
			// notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
}

/***********************************************************
 * ACTION LISTENERS*****************
 * *********************************************************/

function hideFavoredEditCompetences() {
	$("#favored-editing").hide();
	$(".cv-overlay").hide();
	$('.section').removeAttr('style');

	if (favoredCompetences.length == 0) {
		$('#empty-favored').show();
		$("#favored-tags").hide();
	} else {
		$('#empty-favored').hide();
		$("#favored-tags").show();
	}
	$("#favored-tags-editing").tagit("removeAll");
	for (var i = 0; i < favoredCompetences.length; i++) {
		$("#favored-tags-editing").tagit("createTag", favoredCompetences[i]);
	}
}

function btnEditFavoredAction() {
	section = 0;
	if (isEditingFavored) {
		hideFavoredEditCompetences();

	} else {
		$("#favored-tags-editing").tagit("removeAll");
		for (var i = 0; i < favoredCompetences.length; i++) {
			$("#favored-tags-editing").tagit("createTag", favoredCompetences[i]);
		}

		$('#empty-favored').hide();
		$("#favored-tags").hide();
		$("#favored-editing").show();

		$(".cv-overlay").show();
		$(".section#section-favored-competences").css({
			"z-index" : 999
		});
	}

	isEditingFavored = !isEditingFavored;
}

function btnEditExperienceAction() {
	section = 1;
	if (isEditingExperience) {
		$("#btn-add-container-experience").fadeIn();
		$(".cv-overlay").hide();
		$('.section').removeAttr('style');
	} else {
		$("#btn-add-container-experience").hide();
		$(".cv-overlay").show();
		$(".section#section-experiences").css({
			"z-index" : 999
		});
	}
	isEditingExperience = !isEditingExperience;
	$("#experience-editing").slideToggle(600, "easeOutExpo");

}

function btnCancelEditExperienceAction() {
	isEditingExperience = false;
	$("#btn-add-container-experience").fadeIn();
	$("#experience-editing").slideToggle(600, "easeOutExpo");

	var title = $("#experience-title").val("");
	var company = $("#experience-company").val("");
	var startDate = $("#startDateExp").val("");
	var endDate = $("#endDateExp").val("");
	var description = $("#descExp").val("");
	$(".cv-overlay").hide();
	$('.section').removeAttr('style');
}

function hideEditingCompetences() {
	$("#competences-editing").hide();

	$(".cv-overlay").hide();
	$('.section').removeAttr('style');

	if (competences.length == 0) {
		$('#empty-competences').show();
		$("#competences-tags").hide();
	} else {
		$('#empty-competences').hide();
		$("#competences-tags").show();
	}
	$("#competences-tags-editing").tagit("removeAll");
	for (var i = 0; i < competences.length; i++) {
		$("#competences-tags-editing").tagit("createTag", competences[i]);
	}
}

function btnEditCompetencesAction() {
	section = 2;
	if (isEditingCompetence) {
		hideEditingCompetences()

	} else {
		$("#competences-tags-editing").tagit("removeAll");
		for (var i = 0; i < competences.length; i++) {
			$("#competences-tags-editing").tagit("createTag", competences[i]);
		}

		$('#empty-competences').hide();
		$("#competences-tags").hide();
		$("#competences-editing").show();

		$(".cv-overlay").show();
		$(".section#section-competences").css({
			"z-index" : 999
		});
	}

	isEditingCompetence = !isEditingCompetence;
}

function btnEditFormationAction() {
	section = 3;
	if (isEditingFormation) {
		$("#btn-add-container-formation").fadeIn();
		$(".cv-overlay").hide();
		$('.section').removeAttr('style');
	} else {
		$("#btn-add-container-formation").hide();
		$(".cv-overlay").show();
		$(".section#section-formation").css({
			"z-index" : 999
		});
	}
	isEditingFormation = !isEditingFormation;
	$("#formation-editing").slideToggle(600, "easeOutExpo");
}

function btnCancelEditFormationAction() {
	isEditingFormation = false;
	$("#btn-add-container-formation").fadeIn();
	$("#formation-editing").slideToggle(600, "easeOutExpo");

	var school = $("#formation-location").val("");
	var diploma = $("#formation-diploma").val("");
	var startDate = $("#startDateFormation").val("");
	var endDate = $("#endDateFormation").val("");

	$(".cv-overlay").hide();
	$('.section').removeAttr('style');

}

function btnEditLanguageAction() {
	section = 4;
	if (isEditingLanguage) {
		$("#btn-add-container-language").fadeIn();
		$(".cv-overlay").hide();
		$('.section').removeAttr('style');
	} else {
		$("#btn-add-container-language").hide();
		$(".cv-overlay").show();
		$(".section#section-languages").css({
			"z-index" : 999
		});
	}
	isEditingLanguage = !isEditingLanguage;
	$("#language-editing").slideToggle(600, "easeOutExpo");
}

function btnCancelEditLanguageAction() {
	isEditingLanguage = false;
	$("#btn-add-container-language").fadeIn();
	$("#language-editing").slideToggle(600, "easeOutExpo");

	$("#language-name").val("");
	$("#language-level").val("");

	$(".cv-overlay").hide();
	$('.section').removeAttr('style');
}

function hideCvEdit() {
	// $(".cv-overlay").hide();
	// $(".section:not(#section-favored-competences").css({
	// "-webkit-filter" : " blur(2px)"
	// })

	switch (section) {
	case 0:
		btnEditFavoredAction();
		break;
	case 1:
		btnCancelEditExperienceAction();
		break;
	case 2:
		btnEditCompetencesAction();

		break;
	case 3:
		btnCancelEditFormationAction();

		break;
	case 4:
		btnCancelEditLanguageAction();

		break;
	}
}

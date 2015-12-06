// Offres d'emplois
var tab;

// Infos utilisateur

var token = $.cookie(Cookie.TOKEN);
//
$(document).ready(function() {

	/** Loading topHeader html template and the script * */
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
		$("#tab2").addClass("tab-selected");

	})

	$("#offers-container").on("click", ".btn-remove", function() {
		offer = $(this).parent().parent().parent();
		showConfirmDialog("Voulez vous vraiment supprimer cette offre ??", bDeleteAction, "#F2E3E3");
	});

	// "No" button handler
	$("#offers-container").on("click", ".b_no", hideConfirmDialog);

	// tab = [ {
	// id : "87569786050875860688760897697657khb",
	// type : "Développeur Informatiqueeee",
	// location : "Bayonne",
	// date : "27 Février 2020",
	// description : "Ceci est une description de cette offre tres intéressante
	// pour vous..."
	// }, {
	// id : "kjgj87576576476R98798769679897687",
	// type : "Développeur Informatique",
	// location : "Lyon",
	// date : "27 Février 2020",
	// description : "Ceci est une description de cette offre tres intéressante
	// pour vous..."
	// } ];
	//
	// for (i = 0; i < tab.length; i++) {
	// $("#offers-container").loadTemplate("/pstl/templates/my_offer.html",
	// tab[i], {
	// append : true
	// });
	// }

	getAllUserOffers();

	$("#offers-container").on("click", ".btn-detail", function(event) {
		offer = $(this).parent().parent().parent();
		window.location.href = "my_offer.html?offer_id=" + offer.attr("offer_id");
	})

	$("#offers-container").on("click", ".btn-candidats-p", function(event) {

		offer = $(this).parent().parent().parent();
		showPotentialDialog();
		getPotentialCandidats();
	});

});

function showConfirmDialog(message, onConfirm, bgColor) {
	offer.children(".offer-menu").children(".offer_confirmation").children(".content_text").text(message);
	offer.children(".offer-menu").children(".offer_confirmation").addClass("show_offer_confirmation");

	offer.children(".offer-menu").children(".offer_confirmation").children(".b_yes").off();
	offer.children(".offer-menu").children(".offer_confirmation").children(".b_yes").click(onConfirm);

	$("body").append("<div class='pcd_overlay'></div>");
	$(".pcd_overlay").click(hideConfirmDialog);
	offer.css({
		"z-index" : 999
	});
	$(".offer_confirmation").css({
		"background" : bgColor
	});
}

function hideConfirmDialog() {
	offer.children(".offer-menu").children(".offer_confirmation").removeClass("show_offer_confirmation");

	offer.children(".offer-menu").show();
	offer.children(".potentials-user-container").slideUp(600, "easeOutExpo");

	$(".pcd_overlay").remove();
	offer.css({
		"z-index" : 100
	});

}

function showPotentialDialog() {
	offer.children(".offer-menu").hide();
	offer.children(".potentials-user-container").stop(true).slideDown(600, "easeOutExpo");

	$("body").append("<div class='pcd_overlay'></div>");
	$(".pcd_overlay").click(hideConfirmDialog);
	offer.css({
		"z-index" : 999
	});
}

function bDeleteAction() {

	$.ajax({
		type : "GET",
		url : "delete_offer.json",
		dataType : "json",
		data : {
			token : token,
			id : offer.attr("offer_id")
		},
		success : function(data) {
			if (data.ok) {
				offer.slideUp(600, "easeOutExpo");
				notify("L'offre a bien été supprimé", Notif.DELAY);
				hideConfirmDialog();

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

function getPotentialCandidats() {

	$.ajax({
		type : "GET",
		url : "get_potential_candidats.json",
		dataType : "json",
		data : {
			token : token,
			id : offer.attr("offer_id")
		},
		success : function(data) {
			if (!(data.error_code == ErrorCodes.SESSION_EXPIRED)) {
				offer.children().children("ul").text("");
				for (var i = 0; i < data.length; i++)
					offer.children().children("ul").append("<li><a>" + data[i].first_name + " " + data[i].last_name + "</a><div>" + data[i].score + "</div></li>");

			} else
				notifyError("Un probleme est survenu :(");
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});

}

function onSuccess(tab) {
	for (i = 0; i < tab.length; i++) {
		$("#offers-container").loadTemplate("/pstl/templates/my_offer.html", tab[i], {
			append : true,
		});
	}
}

function getAllUserOffers() {
	$.ajax({
		type : "GET",
		url : "get_allUser_offer.json",
		dataType : "json",
		data : {
			token : token
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code)
				notifyError("Un probleme est survenu :(");
			else {
				onSuccess(data);
			}

		},
		error : function(data) {
			notifyError("error" + data);
		},
	});
}

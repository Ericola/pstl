/*********************************************************/
/******					GLobal variables		**********/
/** ****************************************************** */
var token = $.cookie('token');
var id = $.cookie(Cookie.ID);
var settingGroupEditing;
var currentFieldContent;

/** ****************************************************** */
/** **** Document ready ********* */
/** ****************************************************** */
$(document).ready(function() {

	/*** Chargement de topHeader *****/
	$("#top-header").load("top_header.html", function() {
		$("#scripts").append('<script type="text/javascript" src="js/top_header.js"></script>');
	})

	animateDelayed("settings-group");
	$(".settings-overlay").click(hideSettingsEdit);
	$.cookie(Cookie.LOGGED, 'true');
	$.ajax({
		type : "GET",
		url : "/pstl/get_user_infos",
		dataType : "json",
		data : {
			token : token,
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code) {
				notifyError("Un probleme est survenu :(");
				logout();
			} else {
				$("#login span.settings-content").text(data.username);
				$("#login .settings-edit .value").val(data.username);

				$("#password span.settings-content").text("***********");

				$("#last-name span.settings-content").text(data.last_name);
				$("#last-name .settings-edit .value").val(data.last_name);

				$("#first-name span.settings-content").text(data.first_name);
				$("#first-name .settings-edit .value").val(data.first_name);

				$("#mail span.settings-content").text(data.email);
				$("#mail .settings-edit .value").val(data.email);

			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});

	$("form").on("submit", function(event) {

		var column = "";
		switch (settingGroupEditing) {
		case "login":
			column = "username";
			break;
		case "password":
			column = "password";
			break;
		case "last-name":
			column = "last_name";
			break;
		case "first-name":
			column = "first_name";
			break;
		case "mail":
			column = "email";
			break;
		}
		var value = $("#" + settingGroupEditing + " .settings-edit .value").val();
		var password = $("#" + settingGroupEditing + " .settings-edit .current-password").val();
		var confirmation = $("#" + settingGroupEditing + " .settings-edit .confirm-password").val();

		if (settingGroupEditing == "password" && value != confirmation)
			notifyError("Les mots de passe ne correspondent pas!");
		else {
			var pass = $.md5(password);
			$.ajax({
				type : "GET",
				url : "/pstl/update_user",
				dataType : "json",
				data : {
					token : token,
					column : column,
					value : value,
					password : pass

				},
				success : function(data) {
					if (data.ok) {
						notify("Les modifications ont été enregistrées", Notif.DELAY);

						// update content display
						hideSettingsEdit(settingGroupEditing);
						var newContent = $("#" + settingGroupEditing + " .settings-edit .value").val();
						$("#" + settingGroupEditing + " .settings-content:not(.content-password)").text(newContent);

						if (column == "last_name")
							$.cookie(Cookie.LAST_NAME, value);
						else if (column == "first_name")
							$.cookie(Cookie.FIRST_NAME, value);
						$(".user span").text($.cookie(Cookie.FIRST_NAME) + " " + $.cookie(Cookie.LAST_NAME));

					} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
						logout();
					else if (data.error_code == ErrorCodes.BAD_PASSWORD)
						notifyError("Mot de passe incorrect");
					else
						notifyError("Un probleme est survenu :(");
				},
				error : function(data) {
					notifyError("error" + data);
				},

			});
		}
		event.preventDefault();
	});

	$(".btn-cancel").on("click", function(event) {
		hideSettingsEdit(settingGroupEditing);
		$("#" + settingGroupEditing + " .settings-edit .value").val(currentFieldContent);

		event.preventDefault();
	});

	/** Edit button handlers* */
	/** ********************* */
	$("#login button.btn-edit").click(function(event) {
		settingGroupEditing = "login";
		showSettingsEdit();
		event.preventDefault();

	});

	$("#password button.btn-edit").click(function(event) {
		settingGroupEditing = "password";
		showSettingsEdit();
		event.preventDefault();

	});

	$("#last-name button.btn-edit").click(function(event) {
		settingGroupEditing = "last-name";
		showSettingsEdit();
		event.preventDefault();

	});

	$("#first-name button.btn-edit").click(function(event) {
		settingGroupEditing = "first-name";
		showSettingsEdit();
		event.preventDefault();

	});

	$("#mail button.btn-edit").click(function(event) {
		settingGroupEditing = "mail";
		showSettingsEdit();
		event.preventDefault();

	});

	$(".form-search").submit(function(event) {
		goToPage();
		event.preventDefault();
	});

});

/** ****************************************************** */
/** **** Functions ********* */
/** ****************************************************** */
function showSettingsEdit() {
	settingGroupEditing == "password" ? $("#" + settingGroupEditing).css({
		"height" : "180px"
	}) : $("#" + settingGroupEditing).css({
		"height" : "150px"
	});
	$(".settings-overlay").show();
	$("#" + settingGroupEditing).css({
		"z-index" : 999
	});
	$("#" + settingGroupEditing + " .settings-edit").addClass("show-settings-edit");
	$("#" + settingGroupEditing + " span.settings-content").addClass("hide-settings-content");
	$("#" + settingGroupEditing + " button.btn-edit").addClass("hide-btn-edit");
	currentFieldContent = $("#" + settingGroupEditing + " .settings-edit .value").val();

}

function hideSettingsEdit() {
	$("#" + settingGroupEditing + " .settings-edit").removeClass("show-settings-edit");
	$("#" + settingGroupEditing + " span.settings-content").removeClass("hide-settings-content");
	$("#" + settingGroupEditing + " button.btn-edit").removeClass("hide-btn-edit");
	$("#" + settingGroupEditing).css({
		"height" : "60px"
	});
	$("#" + settingGroupEditing + " .settings-edit .current-password").val("");
	$("#" + settingGroupEditing + " .settings-edit .confirm-password").val("");
	$("#" + settingGroupEditing + " .settings-edit .new-password").val("");
	$(".settings-overlay").hide();
	$("#" + settingGroupEditing).css({
		"z-index" : 4
	});
}

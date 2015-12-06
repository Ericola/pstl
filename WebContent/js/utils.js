/** Servlet errorCodes * */
var ErrorCodes = {
	ARGUMENT : -1,
	JSON : 100,
	SQL : 1000,
	JAVA : 10000,
	SESSION_EXPIRED : 637,
	UNKNOW_USER : 1,
	BAD_PASSWORD : 2,
	USER_EXISTS : 3
};

var UserType = {
	EMPLOYEUR : 1,
	CANDIDAT : 0

}

var Cookie = {
	TOKEN : 'token',
	ID : 'id',
	USER_NAME : 'username',
	LOGGED : 'logged',
	TYPE : 'type',
	LAST_NAME : 'last_name',
	FIRST_NAME : 'first_name'
};

var Notif = {
	DELAY : 10000
};

var notify_timeout;
/** Notification function * */
function notify(message, delay) {

	$("#notification").stop(true);
	clearTimeout(notify_timeout);

	$("#notification").css({
		background : '#D2E4CD',
		'border-color' : '#AFC1AA',
		'color' : '#53694D'

	});
	$("#notification").html(message);
	$("#notification").slideDown(600, "easeOutExpo");

	if (delay != null) {
		notify_timeout = setTimeout(function() {
			$("#notification").slideUp(600, "easeOutExpo");
		}, delay);
	}
}

function notifyError(message, delay) {
	$("#notification").html(message);
	if ($("#notification").css("display") != "none") {
		$("#notification").addClass("shake-notif");
		setTimeout(function() {
			$("#notification").removeClass("shake-notif");
		}, 500);
	}
	$("#notification").stop(true);
	clearTimeout(notify_timeout);

	$("#notification").css({
		background : '#EED0D0',
		'border-color' : '#B99191',
		'color' : '#8C4A4A'
	});
	$("#notification").slideDown(600, "easeOutExpo");

	if (delay != null) {
		notify_timeout = setTimeout(function() {
			$("#notification").slideUp(600, "easeOutExpo");
		}, delay);
	}

}

function logout() {
	$.cookie(Cookie.TOKEN, null);
	$.cookie(Cookie.ID, null);
	$.cookie(Cookie.LOGIN, null);
	$.cookie(Cookie.TYPE, null);
	$.cookie(Cookie.NAME, null);
	$.cookie(Cookie.LOGGED, null);
	window.location.href = "index";

}

function animateDelayed(className) {

	$("." + className).each(function(i, elmt) {

		setTimeout(function() {
			$(elmt).css({
				"visibility" : "visible",
				"opacity" : "1",
				"transform" : "translateX(0)",
				"transform" : "translateY(0)"
			});
		}, 1 + i * 50);
	});

}

function goToPage() {
	content = $("#search-text").val();
	window.location.href = 'search.jsp?content=' + content;
}

function levelLangugeToValue(level) {
	switch (level) {
	case 0:
		return "débutant";
	case 1:
		return "intermédiaire";
	case 2:
		return "courant";
	case 3:
		return "bilingue";

	default:
		return "";
	}
}

function getUrlParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
}
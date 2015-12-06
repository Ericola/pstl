animateDelayed("anim");
tyype = parseInt($.cookie('type'));

// candidat view
switch (tyype) {
case UserType.CANDIDAT:
	$(".tabs").load("/pstl/templates/candidat_tab.html", function() {
		// alert(window.location.href);
	})

	break;
case UserType.EMPLOYEUR:
	$(".tabs").load("/pstl/templates/employer_tab.html")

	break;
default:
	$(".tabs").load("/pstl/templates/visitor_tab.html")

	break;
}
var logged = $.cookie(Cookie.LOGGED);
if (logged != 'null' && logged != null){
	$(".user span").text($.cookie(Cookie.FIRST_NAME) + " " + $.cookie(Cookie.LAST_NAME));
	$(".register-connexion").hide();
	$(".user-container").show();

}else{
	$(".register-connexion").show();
	$(".user-container").hide();
}

$('html').click(function() {
	// Hide the menus if visible
	hideUserMenu();
});

$(".user-container").click(function(event) {
	event.stopPropagation();
	openUserMenu();
});

/** ********************************************************* */
/** *** CHARGEMENT DE LA PAGE SETTINGS ************** */
/** ********************************************************* */
$(".btn-settings").click(function(event) {

	// _link = $(this).attr("href");
	// history.pushState(null, null, _link);
	//        
	// // Chargement de la feuille de style --
	// $('head').append('<link rel="stylesheet"
	// href="styles/settings.css" type="text/css" />', function() {
	// });
	//
	// // Chargement de la page --
	// $("#main-zone").load("settings.html", function() {
	// // Chargement du script
	// $.getScript("js/settings.js");
	// });
	//		
	window.location.href = "settings";

});

// $(window).bind("popstate", function() {
// link = location.pathname.replace(/^.*[\\/]/, ""); // get filename
// // only
// $("#main-zone").load(link + ".html");
// });

/** button logout action * */
$(".btn-logout").click(function(event) {
	token = $.cookie('token');
	$.ajax({
		type : "GET",
		url : "/pstl/logout",
		dataType : "json",
		data : {
			token : token,
		},
		success : function(data) {

			if (data.ok) {
				logout();
			} else if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code)
				notifyError("Un probleme est survenu :(");

		},
		error : function(data) {
			alert("error" + data);
		},
	});
});

function openUserMenu() {
	$(".user-container").addClass("expanded");

	$(".menu-container").addClass("menu-displayed");

}

function hideUserMenu() {

	$(".menu-container").removeClass("menu-displayed");

	$(".user-container").removeClass("expanded");

}

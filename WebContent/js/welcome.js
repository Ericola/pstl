var token = $.cookie('token');
var id = $.cookie(Cookie.ID);
$(document).ready(function() {
	var isAlreadyLoggedIn = $.cookie(Cookie.LOGGED);

	$.cookie(Cookie.LOGGED, 'true');
	$.ajax({
		type : "GET",
		url : "/cordobes_zamorano/get_user_infos",
		dataType : "json",
		data : {
			token : token,
		},
		success : function(data) {
			if (data.error_code == ErrorCodes.SESSION_EXPIRED)
				logout();
			else if (data.error_code){
				notifyError("Un probleme est survenu :(");
				logout();
			}
			else {
				if (isAlreadyLoggedIn == null || isAlreadyLoggedIn == 'null') {
					notify("Bonjour Mr " + data.Nom + " " + data.Prenom, Notif.DELAY);
				}
				$("#profil").append(data.Prenom + " " + data.Nom);

			}
		},
		error : function(data) {
			notifyError("error" + data);
		},

	});
	


});



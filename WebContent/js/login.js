var token = $.cookie('token');
if (token != 'null' && token != null)
	window.location.href = "index";

$(document).ready(function() {

	animateDelayed("login-container");
	/**
	 * On connect button click
	 */
	$("#form").submit(function(event) {
		var username = $("#username").val();
		var password = $("#password").val();
		var pass = $.md5(password);
		if (username != "" && password != "") {
			$(".loader").slideDown(600, "easeOutExpo");
			$.ajax({
				type : "POST",
				url : "login.json",
				dataType : "json",
				data : {
					username : username,
					password : pass
				},
				success : function(data) {
					$(".loader").slideUp(600, "easeOutExpo");
					if (data.error_code == ErrorCodes.UNKNOW_USER)
						notifyError("This username doesn't exist");
					else if (data.error_code == ErrorCodes.BAD_PASSWORD)
						notifyError("Incorrect password");
					else if (data.token) {
						$.cookie(Cookie.TOKEN, data.token);
						$.cookie(Cookie.ID, data.id);
						$.cookie(Cookie.USER_NAME, data.username);
						$.cookie(Cookie.TYPE, data.type);
						$.cookie(Cookie.FIRST_NAME, data.first_name);
						$.cookie(Cookie.LAST_NAME, data.last_name);
						$.cookie(Cookie.LOGGED, true);
						window.location.href = "index";
					} else
						notifyError("An error has occurred :(");

				},
				error : function(data) {
					notifyError("error" + data);
				},

			});
		} else {
			if (username == "" || password == "") {
				notifyError("You must enter a username and password!");
			}
		}

		event.preventDefault();

	});

});

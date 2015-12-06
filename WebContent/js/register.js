USER_EXISTS = 3;
var token = $.cookie('token');
if (token != 'null' && token != null)
	window.location.href = "index";

$(document).ready(function() {
	animateDelayed("register-container");

	$("#form").submit(function(event) {
		var username = $("#username").val();
		var email = $("#email").val();
		var lastName = $("#last_name").val();
		var firstName = $("#first_name").val();
		var password = $("#password").val();
		var confirmation = $("#password_conf").val();
		var type = $("#type").val();

		if (username != "" && password != "" && confirmation != "") {
			if (password != confirmation) {
				notifyError("Les mots de passe ne correspondent pas!");
			} else {
				var pass = $.md5(password)
				$.ajax({
					type : "POST",
					url : "create_user.json",
					dataType : "json",
					data : {
						username : username,
						password : pass,
						firstName : firstName,
						lastName : lastName,
						email : email,
						type : type
					},
					success : function(data) {
						if (data.error_code == ErrorCodes.USER_EXISTS)
							notifyError("Uername already exists");
						else if (data.ok) {
							registerSuccess(username, pass);
						} else
							notifyError("An error has occurred :(");
					},
					error : function(data) {
						notifyError("error" + data);
					},
				});
			}
		} else {
			if (username == "" || email == "" || password == "" || confirmation == "" || firstName == "" || lastName == "") {
				notifyError("Please fill in the required fields");
			}
		}

		event.preventDefault();

	});

	function registerSuccess(username, password) {
		$.ajax({
			type : "POST",
			url : "login.json",
			dataType : "json",
			data : {
				username : username,
				password : password
			},
			success : function(data) {
				$.cookie(Cookie.TOKEN, data.token);
				$.cookie(Cookie.ID, data.id);
				$.cookie(Cookie.USER_NAME, data.username);
				$.cookie(Cookie.TYPE, data.type);
				$.cookie(Cookie.FIRST_NAME, data.first_name);
				$.cookie(Cookie.LAST_NAME, data.last_name);
				$.cookie(Cookie.LOGGED, true);
				window.location.href = "index";

			},
		});
		event.preventDefault();
	}

});

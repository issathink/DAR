var m;

isConnected(responseIsConnected);


function responseIsConnected(response) {
	if(response.ok != undefined) {
		console.log("already connected");
		window.location.href = "home.html";
	} else {
		console.log("Noppppppp!");
	}
}

function validate() {

	login = document.forms["signin"]["login"].value;
	pwd  = document.forms["signin"]["pwd"].value;

	document.body.className += "loading";
	// valMail = validateEmail(mail)

	if(login.length >= 6 && pwd.length >= 6) {
		console.log("pwd :" + pwd + " login: " + login);
		signin(login, pwd);
		$("#error_holder").text("");
	} else if(login.length < 6) {
		$("#error_holder").text("Your login is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else if(pwd.length < 6) {
		$("#error_holder").text("Your password is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else {
		$("#error_holder").text("I dont know what is this error.").fadeIn('fast');
		document.body.className = '';
	}
}


function signin(login, pwd) {

	$.ajax({
		url : "../signin",
		type : "get",
		data : "format=json" + "&login=" + login + "&pw=" + pwd,
		dataType : "json",
		callback : responseSignin,
		success : function(response) {
			responseSignin(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			// func_erreur(-1, jqXHR.responseText, errorThrown);
			document.body.className = '';
		}
	});
	console.log("End login.");

}

function responseSignin(response) {
	$("#error_holder").fadeOut('fast');
	console.log("Retour: " + JSON.stringify(response));

	if(response.ok != undefined) {
		// Successfully logged in
		$("#error_holder").text("Bravo! You're now logged in.").fadeIn('fast');
		document.body.className = '';
		setCookie(C_NAME, response.key, 30);
		window.location.href = "home.html";
	} else {
		// Something wrong
		$("#error_holder").text(response.message).fadeIn('fast');
		document.body.className = '';
	}
}
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

	mail = document.forms["signup"]["email"].value;
	login = document.forms["signup"]["login"].value;
	pwd = document.forms["signup"]["pwd"].value;
	repwd = document.forms["signup"]["repwd"].value;

	console.log("mail: " + mail);
	console.log("pwd: " + pwd);
	console.log("repwd: " + repwd);

	document.body.className += "loading";
	m = mail;
	rep = repwd;
	valMail = validateEmail(mail);
	$("#error_holder").fadeOut('fast');

	if(valMail == true && pwd == repwd && pwd.length >= 6) {		
		signup(mail, login, pwd, repwd);
	} else if(valMail == false) {
		$("#error_holder").text("Please enter a valid email.").fadeIn('fast');
		document.body.className = '';
	} else if(login.length < 6) {
		$("#error_holder").text("Your login is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else if(pwd.length < 6) {
		$("#error_holder").text("Your password is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else if(pwd != repwd) {
		$("#error_holder").text("Passwords doesn't match.").fadeIn('fast');
		document.body.className = '';
	} 
}


function signup(mail, login, pwd, repwd) {

	$.ajax({
		url : "../signup",
		type : "get",
		data : "format=json" + "&mail=" + mail + "&login=" + login + "&pw=" + pwd + "&repw=" + repwd,
		dataType : "json",
		callback : responseSignup,
		success : function(rep) {
			responseSignup(rep);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			func_erreur(-1, jqXHR.responseText, errorThrown);
		}
	});

}


function responseSignup(response) {

	if(response.ok != undefined) {
		$("#error_holder").fadeOut('fast');
		$("#error_holder").text(response.message).fadeIn('fast');

		setCookie(C_NAME, response.key, 30);
		window.location.href = "home.html"
	} else {
		$("error_holder").text("Unexpected error. Try again").fadeIn('fast');
		console.log("What is this: " + JSON.stringify(response));
	}

	document.body.className = '';
}

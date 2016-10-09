var m;

isConnected(responseIsConnected);


function responseIsConnected(response) {
	if(response.ok) {
		console.log("already connected");
		window.location.href = "home.html";
	} else {
		console.log("Noppppppp!");
	}
}

function validate() {

	mail = document.forms["signin"]["email"].value;
	pwd  = document.forms["signin"]["pwd"].value;

	m = mail
	console.log("mail: " + mail);
	console.log("pwd: " + pwd);

	document.body.className += "loading";
	valMail = validateEmail(mail)

	if(valMail == true && pwd.length >= 6) {
		console.log("pwd :" + pwd + " mail: " + m);
		login(m, pwd);
		$("#error_holder").text("");
	} else if(valMail == false) {
		$("#error_holder").text("Please enter valid email.").fadeIn('fast');
		document.body.className = '';
	} else if(pwd.length < 6) {
		$("#error_holder").text("Your password is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else {
		$("#error_holder").text("I dont know what is this error.").fadeIn('fast');
		document.body.className = '';
	}
}


function login(mail, pwd) {

	$.ajax({
		url : "http://vps197081.ovh.net:8080/Issa/signin",
		type : "get",
		data : "format=json" + "&email=" + mail + "&pwd=" + pwd,
		dataType : "json",
		callback : responseLogin,
		success : function(rep) {
			responseLogin(rep, mail);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			// func_erreur(-1, jqXHR.responseText, errorThrown);
			document.body.className = '';
		}
	});
	console.log("End login.");

}

function responseLogin(rep, mail) {
	$("#error_holder").fadeOut('fast');
	console.log("Retour: " + JSON.stringify(rep));

	if(rep.ret_code == 0) {
		// Successfully logged in
		$("#error_holder").text("Bravo! You're now logged in.").fadeIn('fast');
		document.body.className = '';
		setCookie(C_NAME, rep.genId, 30);
		window.location.href = "home.html";
	} else {
		// Something wrong
		$("#error_holder").text(rep.message).fadeIn('fast');
		document.body.className = '';
	}
}
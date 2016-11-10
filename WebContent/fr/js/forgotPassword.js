function sendPassword(){
	mail = document.forms["forgotPassword"]["mail"].value;
	login = document.forms["forgotPassword"]["login"].value;

	document.body.className += "loading";

	if(mail.length > 0 && login.length > 0)  {
		forgotPw(mail, login);
		$("#error_holder").text("");
	} else {
		$("#error_holder").text("Check the length on both fields").fadeIn('fast');
		document.body.className = '';
	}
}

function forgotPw(mail, login){
	$.ajax({
		url : "../forgotPassword",
		type : "get",
		data : "format=json" + "&mail=" + mail + "&login=" + login,
		dataType : "json",
		callback : responseForgotPw,
		success : function(response) {
			responseForgotPw(response, mail, login);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			document.body.className = '';
		}
	});
	console.log("End login.");
}

function responseForgotPw(response){
	$("#error_holder").fadeOut('fast');
	console.log("Retour: " + JSON.stringify(response));
	document.forms["forgotPassword"]["mail"].value = '';
	document.forms["forgotPassword"]["login"].value = '';

	if(response.ok != undefined) {
		// Successfully logged in
		$("#error_holder").text("Password changed.").fadeIn('fast');
		document.body.className = '';
	} else {
		// Something wrong
		$("#error_holder").text(response.erreur).fadeIn('fast');
		document.body.className = '';
	}
}


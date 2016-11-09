function validate() {

	pwd = document.forms["signin"]["pwd_prec"].value;
	pwd_new  = document.forms["signin"]["pwd_new"].value;
	pwd_new_bis  = document.forms["signin"]["pwd_new_bis"].value;
	var session_id = getCookie(C_NAME);

	document.body.className += "loading";

	if(pwd_new.length >= 6 && pwd_new_bis.length >= 6 && pwd_new == pwd_new_bis)  {
		console.log("pwd :" + pwd_new);
		changePw(pwd, pwd_new, session_id);
		$("#error_holder").text("");
	} else if(pwd_new.length < 6) {
		$("#error_holder").text("Your new password is too short (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	} else {
		$("#error_holder").text("Type the same password on both fields.").fadeIn('fast');
		document.body.className = '';
	}
}

function changePw(pwd, pwd_new, session_id) {

	$.ajax({
		url : "../changepw",
		type : "get",
		data : "format=json" + "&prec_pw=" + pwd + "&new_pw=" + pwd_new + "&session_id=" + session_id,
		dataType : "json",
		callback : responseChangePw,
		success : function(response) {
			responseChangePw(response, session_id);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			document.body.className = '';
		}
	});
	console.log("End login.");

}

function responseChangePw(response, session_id) {
	$("#error_holder").fadeOut('fast');
	console.log("Retour: " + JSON.stringify(response));

	if(response.ok != undefined) {
		// Successfully logged in
		$("#error_holder").text("Password changed.").fadeIn('fast');
		document.body.className = '';
		setCookie(C_NAME, session_id, 30);
		window.location.href = "home.html";
	} else {
		// Something wrong
		$("#error_holder").text("Wrong password").fadeIn('fast');
		document.body.className = '';
	}
}

function getInfos(){
	var session_id = getCookie(C_NAME);
	$.ajax({
		url : "../getInfos",
		type : "get",
		data : "format=json" + "&session_id=" + session_id,
		dataType : "json",
		callback : responseGetInfos,
		success : function(response) {
			responseGetInfos(response, session_id);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			document.body.className = '';
		}
	});
}

function responseGetInfos(response) {
	console.log("Retour: " + JSON.stringify(response));
	if(response.ok != undefined) {
		// Successfully loaded
		document.body.className = '';
		document.getElementById("infos").innerHTML = "<pre> Your login : "+response.login
			+"</pre><pre> Your mail : "+response.mail+"</pre>";
	} else {
		// Something wrong
		document.body.className = '';
	}
}


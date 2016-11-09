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
	} else {
		// Something wrong
		$("#error_holder").text("Wrong password").fadeIn('fast');
		document.body.className = '';
	}
}

function deleteNow(){
	pw = document.forms["deleteAccount"]["pwd_delete"].value;
	console.log("pw : "+pw);
	if(pw.length <= 0) {
		$("#error_delete").text("Type your password (at least 6 chars).").fadeIn('fast');
		document.body.className = '';
	}
	else {
		deleteAccount(pw);
	}
}

function deleteAccount(pw){
	var session_id = getCookie(C_NAME);
	$.ajax({
		url : "../deleteAccount",
		type : "get",
		data : "format=json" + "&session_id=" + session_id + "&pw=" + pw,
		dataType : "json",
		callback : responseDeleteAccount,
		success : function(response) {
			responseDeleteAccount(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			document.body.className = '';
		}
	});
}

function responseDeleteAccount(response) {
	console.log("Retour delete: " + JSON.stringify(response));
	if(response.ok != undefined) {
		// Successfully deleted
		logout();
		document.body.className = '';
	} else {
		// Something wrong
		$("#error_delete").text(JSON.stringify(response.erreur)).fadeIn('fast');
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

function initialize() {
	var input = document.getElementById('searchTextField');
	var autocomplete = new google.maps.places.Autocomplete(input);
}

function initMap() {
	var paris = {lat: 48.866667, lng: 2.333333};
	map = new google.maps.Map(document.getElementById('maps'), {
		zoom: 12,
		center: paris
	});
	google.maps.event.addDomListener(window, 'load', initialize);
}

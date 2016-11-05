var adresse = window.location.href.split('=')[1];
var note;
isConnected(responseIsConnected);
getCommentsAndNote(adresse);


function responseIsConnected(response) {
	console.log(response);
	console.log(adresse);
	if(response.ok != undefined) {
		console.log("already connected");

		document.getElementById("top_button").innerHTML = "<button type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-envelope' aria-hidden='true'></a> </button>" +
		"<button type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>";

	} else {
		document.getElementById("top_button").innerHTML = "<div class='depl_haut'> <a href='signin.html'>Se connecter</a></div>";
	}
}


function changePage() {
	addr = document.getElementById("searchTextField").value;
	if(addr.length > 0)
		window.location.href = "home.html?adresse=" + addr;
}


function getCommentsAndNote(adresse) {
	if(adresse != undefined){
		$.ajax({
			url : "http://vps197081.ovh.net:8080/DAR/getCommentsAndNote?",
			type : "GET",
			data : "adresse=" + adresse,
			dataType : "json",
			success : function(rep) {
				responseSetCommentsAndNote(rep, adresse);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "setContact");
			}
		});
	}
}


function responseSetCommentsAndNote(rep, adresse) {
	if(rep.erreur != undefined) {
		if(rep.erreur.indexOf("Invalid address") !== -1) {
			//setVisible(choose another adress)
			console.log("Not in Paris or its suburban");
		}
		else
			console.log("Noppp unknown error!");
	}
	else {
		adresse = decodeURI(adresse)

		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...' name='comment' id='commentText'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='submit'>Send</button> </span> </div>";

		var note = document.getElementById("donner_note");
		note.innerHTML = "Noter : <span id='rateYo'></span> <script type='text/javascript'>";

		document.getElementById("donner_note").style.visibility = "visible";
		$(function() { 
			$('#rateYo').rateYo({ rating : '50%', spacing : '10px', halfStar : true }).on("rateyo.set", function (e, data) {
				note = data.rating/20;
				console.log("Vaut : "+note);
				rate(note);
			});
		}); 

		document.getElementById("h3NomContact").innerHTML = "<b>"+adresse+"</b>";
		var myDiv = document.getElementById("idDivMessages");
		document.getElementById("com").style.visibility = "visible"
		var note = document.getElementById("moyenne");
		note.innerHTML = "Note moyenne : <span id='rateYo2'></span> <script type='text/javascript'>";

		if(rep.moyenne != undefined) {
			document.getElementById("moyenne").style.visibility = "visible";
			$(function() { 
				$('#rateYo2').rateYo({ rating : rep.moyenne[0].toString() , readOnly : true, spacing : '10px' });
			}); 
		}

		while (myDiv.hasChildNodes()) // Remove l'ancien affichage
			myDiv.removeChild(myDiv.lastChild);

		if(rep.login != undefined) {
			for(var i=0 ; i<rep.login.length ; i++) {
				var login = rep.login[i];
				var message = rep.comment[i];
				var newBalise = document.createElement("p");
				//var num = (login === pseudo_friend) ? 2 : 1;
				newBalise.innerHTML = login + " : " + message;
				newBalise.className ="m m"+1;
				myDiv.appendChild(newBalise);
			}
		}
	}
}

function errorFunction(resultatXHR, statut, erreur, fctName) {
	/*alert("Fonction : "+fctName);
	alert("En erreur : "+erreur);
	alert("XHR = "+resultatXHR.responseText);
	alert("Statut = "+ statut);*/
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Error loading commentsAndNote");
}


function initialize() {
	var input = document.getElementById('searchTextField');
	var autocomplete = new google.maps.places.Autocomplete(input);
}


function initMap() {
	var paris = {lat: 48.866667, lng: 2.333333};
	var map = new google.maps.Map(document.getElementById('maps'), {
		zoom: 8,
		center: paris
	});

	google.maps.event.addDomListener(window, 'load', initialize);
	/*var marker = new google.maps.Marker({
        position: paris,
        map: map
    });*/
}

function comment(rep) {
	var adr = get_ParamGET("adresse");
	var comment = document.getElementById('commentText').value;
	var session_id = getCookie(C_NAME);
	console.log("adr : " + adr + " , com : " + comment + " , session : " + session_id);
	if(session_id == null || session_id == undefined)
		console.log("Pas d'identifiant de session");
	else {
		$.ajax({
			url : "http://vps197081.ovh.net:8080/DAR/comment?",
			type : "GET",
			data : "session_id=" + session_id + "&adresse=" + adr + "&comment=" + comment,
			dataType : "json",
			success : function(rep) {
				responsePostComment(rep);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "postComment");
			}
		});
		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...' name='comment' id='commentText'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='submit'>Send</button> </span> </div>";
	}
}

function responsePostComment(rep) {
	if(rep.erreur != undefined) {
		console.log("ERROR");
		$('#error_caractere').hide();
		$('#error_caractere').fadeIn('fast');
	} else {
		console.log("SUCCEED");
		$('#error_caractere').hide();
		getCommentsAndNote(adresse);
	}
}

function rate(note) {
	var adr = get_ParamGET("adresse");
	var session_id = getCookie(C_NAME);
	console.log("adr : " + adr + " , session : " + session_id + " , note : " + note);
	if(session_id == null || session_id == undefined)
		console.log("Pas d'identifiant de session");
	else {
		$.ajax({
			url : "http://vps197081.ovh.net:8080/DAR/rate?",
			type : "GET",
			data : "session_id=" + session_id + "&adresse=" + adr + "&note=" + note,
			dataType : "json",
			success : function(rep) {
				responsePostRate(rep);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "postRate");
			}
		});
	}
}

function responsePostRate(rep) {
	if(rep.erreur != undefined) {
		console.log("ERROR");
	} else {
		console.log("SUCCEED");
		getCommentsAndNote(adresse);
	}
}

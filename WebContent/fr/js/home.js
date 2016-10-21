var adresse = window.location.href.split('=')[1];
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

		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='button'>Send</button> </span> </div>";

		document.getElementById("donner_note").innerHTML = "<div id='note'>Donner une note :</div>";
		document.getElementById("etoiles").innerHTML = "<div class='stars' id='rateYo'></div> <script type='text/javascript'>" +
		"$(function() { $('#rateYo').rateYo({ rating : '50%', spacing : '10px', }); }); </script>";
	} else {
		console.log("Noppppppp!");
		document.getElementById("top_button").innerHTML = "<div class='depl_haut'> <a href='signin.html'>Se connecter</a></div>";
	}
}

function getCommentsAndNote(adresse) {
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


function responseSetCommentsAndNote(rep, adresse) {
	console.log(rep.login[0]);
	console.log(rep.comment[0]);
	console.log(rep.moyenne[0]);
	document.getElementById("h3NomContact").innerHTML = "<b>"+adresse+"</b>";
	var myDiv = document.getElementById("idDivMessages");
	var note = document.getElementById("moyenne");;
	note.innerHTML = "Note moyenne : <span id='rateYo2'></span> <script type='text/javascript'>" +
		"$(function() { $('#rateYo2').rateYo({ rating : "+ rep.moyenne[0].toString() + ", readOnly : true, spacing : '10px' });" +
		"}); </script>";
	while (myDiv.hasChildNodes()) // Remove l'ancien affichage
		myDiv.removeChild(myDiv.lastChild);
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

function errorFunction(resultatXHR, statut, erreur, fctName) {
	/*alert("Fonction : "+fctName);
	alert("En erreur : "+erreur);
	alert("XHR = "+resultatXHR.responseText);
	alert("Statut = "+ statut);*/
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Error loading commentsAndNote");
}

function initMap() {
    var paris = {lat: 48.866667, lng: 2.333333};
    var map = new google.maps.Map(document.getElementById('maps'), {
        zoom: 8,
        center: paris
    });
    /*var marker = new google.maps.Marker({
        position: paris,
        map: map
    });*/
}

var adresse = window.location.href.split('=')[1];
var login;
var mail;
var adresseDefault = "Place Jussieu, Paris, France";
if(adresse == undefined) {
	window.location.href = "home.html?adresse=" + adresseDefault;
}

var note;
var map;
var dist = formateDistance(document.getElementById("distance-selector").value); //default val of the distance
console.log("Distance = "+dist);
var boolIsConnected = false;
isConnected(responseIsConnected);

if(adresse != undefined) {
	loadAllInfos(adresse, dist, true);
	getCommentsAndNote(adresse, dist);
	//setMarkerOfAdress(map, location, adresse);
}

function initMap() {
	var paris = {lat: 48.866667, lng: 2.333333};
	map = new google.maps.Map(document.getElementById('maps'), {
		zoom: 12,
		center: paris
	});
	google.maps.event.addDomListener(window, 'load', initialize);
}

function initialize() {
	var input = document.getElementById('searchTextField');
	var autocomplete = new google.maps.places.Autocomplete(input);
}


function responseIsConnected(response) {
	console.log(response);
	console.log(adresse);
	if(response.ok != undefined) {
		console.log("already connected");
		boolIsConnected = true;
		document.getElementById("top_button").innerHTML = "<button onclick='goToChat()' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-envelope' aria-hidden='true'></a> </button>" +
		"<button onclick='toInfos()' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>" +
		"<button onclick='disconnect();' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-log-out' aria-hidden='true'></a> </button>";

	} else {
		document.getElementById("top_button").innerHTML = "<div class='depl_haut'> <a href='signin.html'>Se connecter</a></div>";
		boolIsConnected = false;
	}
}


function setFieldsToComment(rep){
	if(rep.ok != undefined){
		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...' name='comment' id='commentText'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='submit'>Send</button> </span> </div>";
		var note = document.getElementById("donner_note");
		note.style.visibility = "visible";
		note.innerHTML = "Noter : <span id='rateYo'></span> <script type='text/javascript'>";
		$(function() { 
			$('#rateYo').rateYo({ rating : '0%', spacing : '10px', fullStar: true}).on("rateyo.set", function (e, data) {
				note = data.rating/20;
				console.log("Vaut : "+note);
				rate(note);
			});
		}); 
	}
}


function changeDist() {
	var d = document.getElementById("distance-selector").value;
	console.log("distance : " + d);
	d = formateDistance(d);
	dist = d;
	if(adresse != undefined) {
		setAffichageDependingOfBox(map, dist);
		topBar("Loading...", false, 2000);
	}
}



function formateDistance(d) {
	if(d === "100m")
		return 100;
	else if(d === "200m")
		return 200;
	if(d === "1km")
		return 1000;
	if(d === "2km")
		return 2000;
	else
		return 500;
}

function getCommentsAndNote(adresse, myDistance) {
	console.log("Recuperation comments distance = "+myDistance);
	if(adresse != undefined){
		$.ajax({
			url : "../getCommentsAndNote?",
			type : "GET",
			data : "adresse=" + adresse+"&distance="+myDistance,
			dataType : "json",
			success : function(rep) {
				responseSetCommentsAndNote(rep, adresse);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "getCommentsAndNote");
			}
		});
	}
}


function responseSetCommentsAndNote(rep, adresse) {
	console.log("Setage des commentaires");
	if(rep.erreur != undefined) {
		if(rep.erreur.indexOf("Invalid address") !== -1) {
			//setVisible(choose another adress)
			console.log("Not in Paris or its suburban");
			topBar("Not in Paris or its suburban", true);
			setTimeout(function(){ window.location.href = "home.html?adresse=" + adresseDefault; }, 2000);
		} else {
			console.log("SetCommentsAndNote error!");
		}
	} else {
		adresse = decodeURI(adresse)

		isConnected(setFieldsToComment);

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

		console.log(rep);
		if(rep.comment != undefined) {
			for(var i=0 ; i<rep.comment.length ; i++) {
				var login = rep.comment[i].login;
				var message = rep.comment[i].comment;
				var addr = rep.comment[i].adresse;
				var dateMess = rep.comment[i].date;
				
				login = myDecodeHTMLspecialhars(login);
				message = myDecodeHTMLspecialhars(message);
				addr = myDecodeHTMLspecialhars(addr);

				addr =  '<span><strong>Adresse :</strong> ' + addr + '</span>';
				var note = rep.comment[i].note;
				if(note !== "")
					note = "| <span><strong>Note :</strong> "+note+"/5</span>";
				var date = "<span>Posté le "+dateMess+"</span>";
				var newBalise = document.createElement("div");
				var tmp = "";
				if(boolIsConnected)
					tmp = 'onclick="goToChat(' + "'" + login +"')" + '"';
				newBalise.innerHTML = '<div class="row"><div class="col-sm-1"><div class="thumbnail">'
					+ '<img '+tmp+' class="img-responsive user-photo" src="https://ssl.gstatic.com/accounts/ui/avatar_2x.png">'
					+ '</div><!-- /thumbnail --></div><!-- /col-sm-1 --><div class="col-sm-10"><div class="panel panel-default"><div class="panel-heading"><strong>'
					+ login + '</strong>, '+date+ ' <br /> ' + addr + ' ' + note + ' </div><div class="panel-body">'
					+ message + '</div><!-- /panel-body --></div><!-- /panel panel-default --></div><!-- /col-sm-5 --></div>';
				myDiv.appendChild(newBalise);
			}
		}
	}
}




function errorFunction(resultatXHR, statut, erreur, fctName) {
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Error loading "+fctName);
	topBar(resultatXHR.responseText.erreur, true);
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
			url : "../comment?",
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
		console.log(rep.erreur);
		$('#error_caractere').hide();
		$('#error_caractere').fadeIn('fast');
		topBar(rep.erreur, true);
	} else {
		console.log("SUCCEED");
		$('#error_caractere').hide();
		getCommentsAndNote(adresse, dist);
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
			url : "../rate?",
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
		topBar(rep.erreur, true);
	} else {
		console.log("SUCCEED");
		getCommentsAndNote(adresse, dist);
	}
}


function topBar(message, err, valTime) {
	if(valTime == undefined)
		valTime = 5000;
	if(err)
		$("<div />", { class: 'erreur_topbar', text: message }).hide().prependTo("body")
		.slideDown('fast').delay(valTime).fadeOut(function() { $(this).remove(); });
	else
		$("<div />", { class: 'ok_topbar', text: message }).hide().prependTo("body")
		.slideDown('fast').delay(valTime).fadeOut(function() { $(this).remove(); });
}

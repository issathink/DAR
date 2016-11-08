var adresse = window.location.href.split('=')[1];
var note;
var map;
var dist = 500;  //default val of the distance
isConnected(responseIsConnected);

if(adresse != undefined) {
	getCommentsAndNote(adresse, dist);
	loadAllInfos(adress, distance)
}

//////////////////////////////////////////////////////

var adressMarker;

var listeMarkerSante;
var listeMarkerSport;
var listeMarkerSecurite;
var listeMarkerEducation;
var listeMarkerTransport;

var markers = [];
markers["sante"] = [];
markers["sport"] = [];
markers["securite"] = [];
markers["education"] = [];
markers["transport"] = [];


var imageSante = 'img/sante2.png';
var imageSport = 'img/sport2.png';
var imageSecurite = 'img/securite2.png';
var imageEducation = 'img/education2.png';
var imageTransport = 'img/transport2.png';


function loadAllInfos(adress, distance) {
	adress = decodeURI(adress);
	$.ajax({
		url : "../search?",
		type : "GET",
		data : "adresse=" + adress + "&apiname=" + "all" + "&dist=" + distance,
		dataType : "json",
		success : function(rep) {
			responseSetAllAPI(rep);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "loadAllInfos");
		}
	});
}

function responseSetAllAPI(rep) {
	var allJson = rep.category;
	console.log("nbElem: " + rep.length);

	var soinJSON = (rep.soin).res;
	var sportJSON = (rep.sport).res;
	var securiteJSON = (rep.securite).res;
	var transportJSON = (rep.transport).res;
	var educationJSON = (rep.education).res;

	setListMarker(location, image);
	markers["sante"] = getListMarkerPerso(jsonArrayApi, imageSante);
	markers["sport"] = getListMarkerPerso(jsonArrayApi, imageSport);
	markers["securite"] = getListMarkerPerso(jsonArrayApi, imageSecurite);
	markers["education"] = getListMarkerPerso(jsonArrayApi, imageEducation);
	markers["transport"] = getListMarkerPerso(jsonArrayApi, imageTransport);
}

function getListMarkerPerso(jsonArrayApi, image) {
	var res = [];
	for(var i=0 ; i<jsonArrayApi.length ; i++) {
		var obj = jsonArrayApi[i];
		var type = obj.type;
		var latitude = obj.latitude;
		var longitude = obj.longitude;
		var location = new google.maps.LatLng(latitude, longitude);
		var nom = obj.nom;
		var description = obj.description;
		var marker = new google.maps.Marker({
			title: nom+" : "+categorie,
			icon: image
		});
		marker.setPosition(location);
		res.push(marker);
	}
	return res;
}

// Ajoute un marqueur sur la map
function afficherListMarkerPerso(myMap, listeMarker) {
	for(var i=0 ; i<listeMarker.length ; i++) {
		var marker = listeMarker[i];
		marker.setMap(myMap);
	}
}

function retirerListMarkerPerso(myMap, listeMarker) {
	for(var i=0 ; i<listeMarker.length ; i++) {
		var marker = listeMarker[i];
		marker.setMap(null);
	}
}

function apiCall(box, name_api) {

	if(box.checked && adresse != undefined) {
		if(name_api === "sport")
			afficherListMarkerPerso(map, markers["sport"]);
		else if(name_api === "sante")
			afficherListMarkerPerso(map, markers["sante"]);
		else if(name_api === "education")
			afficherListMarkerPerso(map, markers["education"]);
		else if(name_api === "securite")
			afficherListMarkerPerso(map, markers["securite"]);
		else if(name_api === "transport")
			afficherListMarkerPerso(map, markers["transport"]);
	}

}


///////////////////////////////////////////////////



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

function setFieldsToComment(rep){
	if(rep.ok != undefined){
		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...' name='comment' id='commentText'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='submit'>Send</button> </span> </div>";
		var note = document.getElementById("donner_note");
		note.style.visibility = "visible";
		note.innerHTML = "Noter : <span id='rateYo'></span> <script type='text/javascript'>";
		$(function() { 
			$('#rateYo').rateYo({ rating : '50%', spacing : '10px', halfStar : true }).on("rateyo.set", function (e, data) {
				note = data.rating/20;
				console.log("Vaut : "+note);
				rate(note);
			});
		}); 
	}
}

function changePage() {
	addr = document.getElementById("searchTextField").value;
	if(addr.length > 0)
		window.location.href = "home.html?adresse=" + addr;
}


function changeDist(rep) {
	var d = document.getElementById("dist").value;
	console.log("distance : " + d);
	dist = d;
	document.getElementById("dist").value = '';
}

function getCommentsAndNote(adresse) {
	if(adresse != undefined){
		$.ajax({
			url : "../getCommentsAndNote?",
			type : "GET",
			data : "adresse=" + adresse,
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
	if(rep.erreur != undefined) {
		if(rep.erreur.indexOf("Invalid address") !== -1) {
			//setVisible(choose another adress)
			console.log("Not in Paris or its suburban");
			topBar("Not in Paris or its suburban", true);
		}
		else
			console.log("SetCommentsAndNote error!");
	}
	else {
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

		if(rep.login != undefined) {
			for(var i=0 ; i<rep.login.length ; i++) {
				var login = rep.login[i];
				var message = rep.comment[i];
				var newBalise = document.createElement("div");
				newBalise.innerHTML = '<div class="row"><div class="col-sm-1"><div class="thumbnail">'
				+ '<img class="img-responsive user-photo" src="https://ssl.gstatic.com/accounts/ui/avatar_2x.png">'
				+ '</div><!-- /thumbnail --></div><!-- /col-sm-1 --><div class="col-sm-10"><div class="panel panel-default"><div class="panel-heading"><strong>'
				+ login + '</strong></div><div class="panel-body">'
				+ message + '</div><!-- /panel-body --></div><!-- /panel panel-default --></div><!-- /col-sm-5 --></div>';
				myDiv.appendChild(newBalise);
			}
		}
	}
}

function errorFunction(resultatXHR, statut, erreur, fctName) {
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Error loading "+fctName);
}


function initialize() {
	var input = document.getElementById('searchTextField');
	var autocomplete = new google.maps.places.Autocomplete(input);
}


function initMap() {
	var paris = {lat: 48.866667, lng: 2.333333};
	map = new google.maps.Map(document.getElementById('maps'), {
		zoom: 10,
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
	} else {
		console.log("SUCCEED");
		getCommentsAndNote(adresse);
	}
}


function apiCall2(box, name_api) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=" + name_api + "&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, name_api);
				}
			});
		}
	}
}


function topBar(message, err) {
	if(err)
		$("<div />", { class: 'erreur_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
	else
		$("<div />", { class: 'ok_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
}



///////////////////////////////////////////////////////////


// Ajoute le marker pour l'adresse et centre la map dessus
function setMakerOfAdress(latlng, myMap) {
	myMap.zoom(4);
	myMap.center(myLatLng);
	adressMarker = new google.maps.Marker({
		position: myLatLng,
		map: myMap,
		title: 'Hello World!' // Mettre l'adresse peut etre
	});

}


function setMarkerCategorie(categorie, myMap) {
	if(markers[categorie] != undefined) {
		for(var i = 0; i < markers[categorie].length; i++) {
			markers[categorie][i].setMap(myMap);
		}
	}
	else {
		// Faire les appel ajax au debut normalement

	}
}

function unsetMarker(categorie) {
	if(markers[categorie] != undefined) {
		for(var i = 0; i < markers[categorie].length; i++) {
			markers[categorie][i].setMap(null);
		}
	}
}


function responseSetAPI(repp) {
	console.log("Dans responseSetAPI !! " + repp);
	var rep = repp.res;
	var categorie = repp.category;
	console.log("nbElem: " + rep.length);
	for(var i=0 ; i<rep.length ; i++) {
		console.log("Debut de boucle "+i);
		var type = rep[i].type;
		var latitude = rep[i].latitude;
		var longitude = rep[i].longitude;
		var location = new google.maps.LatLng(latitude, longitude);
		var nom = rep[i].nom;
		var description = rep[i].description;
		console.log("Location = "+location);
		console.log("categorie = "+categorie);
		console.log("nom = "+nom);
		console.log("description = "+description);
		var image = getImageFromType(type);
		addMarkerPerso(map, location, image, categorie)
		console.log("Fin de boucle "+i);
		console.log("------------------------");
	}
}



function setMarkerBasique(myMap, location) {

	var optionsMarqueur = {
		position: location,
		map: myMap
	};
	var marqueur = new google.maps.Marker(optionsMarqueur);
}




function getImageFromType(type) {
	if(type === "pre_bac" || type === "post_bac")
		return imageEducation;
	else if(type === "pharmacie" || type === "centre_de_soin" || type === "etablissement_hospitalier")
		return imageSante;
	else if(type === "sport")
		return imageSport;
	else if(type === "comissariat")
		return imageSecurite;
	else //if(type === "transport")
		return imageTransport;
}
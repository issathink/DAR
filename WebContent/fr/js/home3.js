var adresse = window.location.href.split('=')[1];
if(adresse == undefined) {
	adresse = "Place Jussieu, Paris, France";
}
var latitudeAddr;
var longitudeAddr;
var adresseLatLnbgIsSet = false;

var note;
var map;
var dist = 500;  //default val of the distance
isConnected(responseIsConnected);

if(adresse != undefined) {
	getCommentsAndNote(adresse, dist);
	loadAllInfos(adresse, dist)
	//setMarkerOfAdress(map, location, adresse);
}


var markersSet = false;

var adressMarker; // Le marqueur de l'adresse

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
	console.log("loadAllInfos ==> AJAX");
	adress = decodeURI(adress);
	$.ajax({
		url : "../search?",
		type : "GET",
		data : "adresse=" + adress + "&apiname=" + "all" + "&dist=" + distance,
		dataType : "json",
		success : function(rep) {
			responseSetAllAPI(rep, map);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "loadAllInfos");
		}
	});
}

function responseSetAllAPI(rep, myMap) {
	console.log("responseSetAllAPI =  +latitudedebut [Appel Ajax pour les Res OK!]");

	var allJson = rep.res;

	latitudeAddr = rep.latitudeAdresse;
	longitudeAddr = rep.longitudeAdresse;
	adresseLatLnbgIsSet = true;

	var myLatLng = new google.maps.LatLng(latitudeAddr, longitudeAddr);
	var tmpAddr = (adresse != undefined)? adresse : "Adresse Not Set";
	console.log("Settage du marker pour l'adresse");
	setMarkerOfAdress(myMap, myLatLng, tmpAddr);

	console.log("responseSetAllAPI latitude = "+latitude);


	var soinJSON = allJson.soin;
	var sportJSON = allJson.sport;
	var securiteJSON = allJson.securite;
	var transportJSON = allJson.transport;
	var educationJSON = allJson.education;

	//setListMarker(location, image);
	markers["sante"] = getListMarkerPerso(soinJSON, imageSante);
	console.log("nbElem sante: " + markers["sante"].length);

	markers["sport"] = getListMarkerPerso(sportJSON, imageSport);
	console.log("nbElem sport: " + markers["sport"].length);

	markers["securite"] = getListMarkerPerso(securiteJSON, imageSecurite);
	console.log("nbElem securite: " + markers["securite"].length);

	markers["education"] = getListMarkerPerso(transportJSON, imageEducation);
	console.log("nbElem education: " + markers["education"].length);

	markers["transport"] = getListMarkerPerso(transportJSON, imageTransport);
	console.log("nbElem transport: " + markers["transport"].length);

	markersSet = true;
	console.log("responseSetAllAPI false");
}

function getListMarkerPerso(jsonArrayApi, image) {
	console.log("DEBUT getListMarkerPerso ==> "+image+" sizeJSONArrayAPi "+jsonArrayApi.length);
	var res = [];
	for(var i=0 ; i<jsonArrayApi.length ; i++) {
		var obj = jsonArrayApi[i];
		var type = obj.type;
		var myLatitude = obj.latitude;
		var myLongitude = obj.longitude;
		var location = new google.maps.LatLng(myLatitude, myLongitude);
		var nom = obj.nom;
		var description = obj.description;
		var marker = new google.maps.Marker({
			title: nom+" : "+description,
			icon: image
		});
		marker.setPosition(location);
		res.push(marker);
	}
	console.log("FIN getListMarkerPerso ==> "+image+" sizeRes "+res.length);
	return res;
}

//Ajoute un marqueur sur la map
function afficherListMarkerPerso(myMap, listeMarker) {
	console.log("afficherListMarkerPerso debut");
	for(var i=0 ; i<listeMarker.length ; i++) {
		var marker = listeMarker[i];
		marker.setMap(myMap);
		console.log("afficherListMarkerPerso fin");
	}
}

//Desaffiche les marqueurs sur la map
function retirerListMarkerPerso(myMap, listeMarker) {
	for(var i=0 ; i<listeMarker.length ; i++) {
		var marker = listeMarker[i];
		marker.setMap(null);
	}
}

//Methode appele lorsqu'on coche ou decoche une case, avec name_api la categoride
function apiCall(box, name_api) {
	console.log("apiCall 1");
	if(!markersSet)
		return;

	console.log("apiCall 2");

	if(box.checked && adresse != undefined) {
		console.log("apiCall 3");
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
	else if(!box.checked && adresse != undefined) {
		console.log("apiCall 4");
		if(name_api === "sport")
			retirerListMarkerPerso(map, markers["sport"]);
		else if(name_api === "sante")
			retirerListMarkerPerso(map, markers["sante"]);
		else if(name_api === "education")
			retirerListMarkerPerso(map, markers["education"]);
		else if(name_api === "securite")
			retirerListMarkerPerso(map, markers["securite"]);
		else if(name_api === "transport")
			retirerListMarkerPerso(map, markers["transport"]);
	}
	console.log("apiCall 5");
}

function setMarkerOfAdress(myMap, myLatLng, adresseString) {
	console.log("setMarkerOfAdress DEBUT");
	adressMarker = new google.maps.Marker({
		position: myLatLng,
		map: myMap,
		zoom: 8,
		title: adresseString // Mettre l'adresse peut etre
	});

	myMap.center(myLatLng);
	console.log("setMarkerOfAdress FIN");
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
		"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>" +
		"<div><span><a href='changePw.html'>Change password?</a>.</span></div>";

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
				responseGetCommentsAndNote(rep, adresse);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "getCommentsAndNote");
			}
		});
	}
}


function responseGetCommentsAndNote(rep, adresse) {
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

		if(rep.moyenne != undefined && rep.moyenne != -1) {
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
				if(message != undefined){
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
	//var adr = get_ParamGET("adresse");
	var session_id = getCookie(C_NAME);
	console.log("adr : " + adresse + " , session : " + session_id + " , note : " + note);
	if(session_id == null || session_id == undefined)
		console.log("Pas d'identifiant de session");
	else {
		$.ajax({
			url : "../rate?",
			type : "GET",
			data : "session_id=" + session_id + "&adresse=" + adresse + "&note=" + note,
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
		console.log(rep.erreur);
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
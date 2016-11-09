var latitudeAddr;
var longitudeAddr;
var adresseLatLnbgIsSet = false;

var markersSet = false;

var adressMarker; // Le marqueur de l'adresse

//http://you.arenot.me/2010/06/29/google-maps-api-v3-0-multiple-markers-multiple-infowindows/

var listeMarkerSante;
var listeMarkerSport;
var listeMarkerSecurite;
var listeMarkerEducation;
var listeMarkerTransport;

var markers = [];
markers["poste"] = [];
markers["sante"] = [];
markers["sport"] = [];
markers["securite"] = [];
markers["education"] = [];
markers["transport"] = [];


var imagePoste = 'img/poste2.png';
var imageSante = 'img/sante2.png';
var imageSport = 'img/sport2.png';
var imageSecurite = 'img/securite2.png';
var imageEducation = 'img/education2.png';
var imageTransport = 'img/transport2.png';


function setAffichageDependingOfBox(myMap) {
	retirerListMarkerPerso(map, markers["poste"]);
	retirerListMarkerPerso(map, markers["sport"]);
	retirerListMarkerPerso(map, markers["sante"]);
	retirerListMarkerPerso(map, markers["education"]);
	retirerListMarkerPerso(map, markers["securite"]);
	retirerListMarkerPerso(map, markers["transport"]);
	markers["poste"] = [];
	markers["sante"] = [];
	markers["sport"] = [];
	markers["securite"] = [];
	markers["education"] = [];
	markers["transport"] = [];
	loadAllInfos(adresse, dist, true);
	getCommentsAndNote(adresse, dist);	
}

function loadAllInfos(adress, distance, withSetAff) { // 3eme setAffichage ou non
	console.log("loadAllInfos ==> AJAX");
	adress = decodeURI(adress);
	$.ajax({
		url : "../search?",
		type : "GET",
		data : "adresse=" + adress + "&apiname=" + "all" + "&dist=" + distance,
		dataType : "json",
		success : function(rep) {
			responseSetAllAPI(rep, map, withSetAff);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "loadAllInfos");
		}
	});
}

function responseSetAllAPI(rep, myMap, withSetAff) {
	console.log("responseSetAllAPI =  +latitudedebut [Appel Ajax pour les Res OK!]");

	var allJson = rep.res;

	latitudeAddr = rep.latitudeAdresse;
	longitudeAddr = rep.longitudeAdresse;
	adresseLatLnbgIsSet = true;

	var myLatLng = new google.maps.LatLng(latitudeAddr, longitudeAddr);
	var tmpAddr = (adresse != undefined)? adresse : "Adresse Not Set";
	console.log("Settage du marker pour l'adresse");
	setMarkerOfAdress(myMap, myLatLng, tmpAddr);

	console.log("responseSetAllAPI latitude = "+latitudeAddr+" longitude = "+longitudeAddr);


	var posteJSON = allJson.poste;
	var soinJSON = allJson.soin;
	var sportJSON = allJson.sport;
	var securiteJSON = allJson.securite;
	var transportJSON = allJson.transport;
	var educationJSON = allJson.education;

	//setListMarker(location, image);
	markers["poste"] = getListMarkerPerso(posteJSON, imagePoste, myMap);
	console.log("nbElem poste: " + markers["poste"].length);
	
	markers["sante"] = getListMarkerPerso(soinJSON, imageSante, myMap);
	console.log("nbElem sante: " + markers["sante"].length);

	markers["sport"] = getListMarkerPerso(sportJSON, imageSport, myMap);
	console.log("nbElem sport: " + markers["sport"].length);

	markers["securite"] = getListMarkerPerso(securiteJSON, imageSecurite, myMap);
	console.log("nbElem securite: " + markers["securite"].length);

	markers["education"] = getListMarkerPerso(educationJSON, imageEducation, myMap);
	console.log("nbElem education: " + markers["education"].length);

	markers["transport"] = getListMarkerPerso(transportJSON, imageTransport, myMap);
	console.log("nbElem transport: " + markers["transport"].length);

	markersSet = true;
	console.log("responseSetAllAPI Fin");

	if(withSetAff) {
		if(document.getElementById("checkBox_sport").checked)
			afficherListMarkerPerso(myMap, markers["sport"]);
		if(document.getElementById("checkBox_sante").checked)
			afficherListMarkerPerso(myMap, markers["sante"]);
		if(document.getElementById("checkBox_education").checked)
			afficherListMarkerPerso(myMap, markers["education"]);
		if(document.getElementById("checkBox_securite").checked)
			afficherListMarkerPerso(myMap, markers["securite"]);
		if(document.getElementById("checkBox_transport").checked)
			afficherListMarkerPerso(myMap, markers["transport"]);
		if(document.getElementById("checkBox_poste").checked)
			afficherListMarkerPerso(myMap, markers["poste"]);
	}
}

function getListMarkerPerso(jsonArrayApi, image, myMap) {
	console.log("DEBUT getListMarkerPerso ==> "+image+" sizeJSONArrayAPi "+jsonArrayApi.length);
	var res = [];

	var infowindow = null;
	infowindow = new google.maps.InfoWindow({
		content: "holding..."
	});

	for(var i=0 ; i<jsonArrayApi.length ; i++) {
		var obj = jsonArrayApi[i];
		var type = obj.type;
		var myLatitude = obj.latitude;
		var myLongitude = obj.longitude;
		var location = new google.maps.LatLng(myLatitude, myLongitude);
		var nom = obj.nom;
		var description = obj.description;

		var markerOptions = {
				title: type,
				icon: image,
				position: location,
				title: "Titre de test"
		};
		var markerPerso = new google.maps.Marker(markerOptions);
		var contentString = nom + ((description !== "") ? " : "+description : "");
		contentString = myDecodeHTMLspecialhars(contentString);
		contentString = "<div><b>" + contentString + "</b></div>";
		console.log("ContentString = "+contentString);


		google.maps.event.addListener(markerPerso,'click', (function(marker,content,infowindow){ 
			return function() {
				infowindow.setContent(content);
				infowindow.open(map,marker);
			};
		})(markerPerso, contentString, infowindow));  

		//////////////////////
		// google.maps.event.addListener(markerPerso, 'click', function () {
		// 	infowindow.setContent(this.html);
		// 	infowindow.open(myMap, this);
		// });

		res.push(markerPerso);
	}
	//////////


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
		if(name_api === "poste")
			afficherListMarkerPerso(map, markers["poste"]);
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
		if(name_api === "poste")
			retirerListMarkerPerso(map, markers["poste"]);
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
		title: adresseString // Mettre l'adresse peut etre
	});

	myMap.setCenter(myLatLng);
	myMap.setZoom(15);
	console.log("setMarkerOfAdress FIN");
}

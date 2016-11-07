var adresse = window.location.href.split('=')[1];
var note;
var map;
var dist = 500;  //default val of the distance
isConnected(responseIsConnected);

getCommentsAndNote(adresse);

// topBar("Wouah test", true); true : error

var imageSante = 'img/sante.png';
var imageSport = 'img/sport.png';
var imageSecurite = 'img/securite.png';
var imageEducation = 'img/education.png';
var imageTransport = 'img/transport.png';



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

		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...' name='comment' id='commentText'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='submit'>Send</button> </span> </div>";

		var note = document.getElementById("donner_note");
		note.innerHTML = "Noter : <span id='rateYo'></span> <script type='text/javascript'>";

		// document.getElementById("donner_note").style.visibility = "visible";
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
				var newBalise = document.createElement("div");
				newBalise.innerHTML = '<div class="row"><div class="col-sm-1"><div class="thumbnail">'
				+ '<img class="img-responsive user-photo" src="https://ssl.gstatic.com/accounts/ui/avatar_2x.png">'
				+ '</div><!-- /thumbnail --></div><!-- /col-sm-1 --><div class="col-sm-10"><div class="panel panel-default"><div class="panel-heading"><strong>'
				+ login + '</strong></div><div class="panel-body">'
				+ message + '</div><!-- /panel-body --></div><!-- /panel panel-default --></div><!-- /col-sm-5 --></div>';
				// var num = (login === pseudo_friend) ? 2 : 1;
				// newBalise.innerHTML = login + " : " + message;
				// newBalise.className ="m m"+1;
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


function transport(box) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=transport&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep, adresse);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, "setTransport");
				}
			});
		}
	}
}

function sport(box) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=sport&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep, adresse);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, "setSport");
				}
			});
		}
	}
}

function sante(box) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=sante&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep, adresse);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, "setSante");
				}
			});
		}
	}
}

function education(box) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=education&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep, adresse);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, "setEducation");
				}
			});
		}
	}
}

function securite(box) {
	if(box.checked){
		if(adresse != undefined){
			adress = decodeURI(adresse);
			console.log(adresse);
			$.ajax({
				url : "../search?",
				type : "GET",
				data : "adresse=" + adresse + "&apiname=securite&dist=" + dist,
				dataType : "json",
				success : function(rep) {
					responseSetAPI(rep, adresse);
				}, 
				error : function(resultatXHR, statut, erreur) {
					errorFunction(resultatXHR, statut, erreur, "setSecurite");
				}
			});
		}
	}
}



var mapCategorie;

function responseSetAPI(repp, adresse) {
	console.log("Dans responseSetAPI !!");
	var rep = repp.res;
	console.log("deb: " + rep.length);
	for(var i=0 ; i<rep.length ; i++) {
		console.log("ouahg: " + i);
		var categorie = rep[i].categorie;
		var type = rep[i].type;
		var latitude = rep[i].latitude;
		var longitude = rep[i].longitude;
		var location = new google.maps.LatLng(latitude, longitude);
		console.log("Location = "+location);
		var nom = rep[i].nom;
		var description = rep[i].description;
		var image = getImageFromType(type);
		setMarkers(map, location, image, categorie)
	}
}

function getImageFromType(type) {
	if(type === "pre_bac" || type === "post_bac")
		return imageEducation;
	else if("pharmacie" || "centre_de_soin" || "etablissement_hospitalier")
		return imageSante;
	else if(type === "sport")
		return imageSport;
	else if(type === "comissariat")
		return imageSecurite;
	else if(type === "transport")
		return imageTransport;
}



function topBar(message, err) {
	if(err)
		$("<div />", { class: 'erreur_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
	else
		$("<div />", { class: 'ok_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
}






function setMarkers(myMap, location, image, categorie) {

	var image = {
		url: image,
    // This marker is 20 pixels wide by 32 pixels high.
    size: new google.maps.Size(20, 32),
    // The origin for this image is (0, 0).
    origin: new google.maps.Point(0, 0),
    // The anchor for this image is the base of the flagpole at (0, 32).
    anchor: new google.maps.Point(0, 32)
};


	var marker = new google.maps.Marker({
		position: location,
		map: myMap,
		icon: image
	});

	mapCategorie[categorie][mapCategorie[categorie].length] = marker;

}

function setMarkerBasique(myMap, location) {}

/*var optionsMarqueur = {
	position: location,
	map: myMap
};
var marqueur = new google.maps.Marker(optionsMarqueur);
}*/
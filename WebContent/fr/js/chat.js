isConnected(responseIsConnectedHeader);
var idUserSession = getCookie(C_NAME); 
// if(idUserSession == null)
// 	idUserSession = "b04b3ab6-79d5-4b82-aeae-0bacdf19f7de1476555095875";
console.log("Chat idSession = "+idUserSession);
var friendLogin = get_ParamGET("friend_login");
var delayRefresh = 3000;


var heightIdDivMessage = document.getElementById("idDivMessages").clientHeight; // (ou offsetHeight) Valeur height du div

/* A l'ouverture de la page */
if(idUserSession == null) {
	console.log("You are not connected");
	topBarChat("You are not connected", true);
	setTimeout(function(){ window.location.href = "signin.html?" }, 2000);
// } else if(friendLogin == null) {
	//alert("Error : 'user_login' = "+userLogin+", friend_login = "+friendLogin);
	//window.location.href = "login.html";
} else {
	refreshPage(); // 1er affichage pour pas voir de latence
	setInterval(function(){refreshPage();}, delayRefresh);
}


/* Liste des fonctions */

function setScrollbar() {
	var obj = document.getElementById("idDivMessages");
	obj.scrollTop = obj.scrollHeight - heightIdDivMessage;
}

function refreshPage() {
	if(friendLogin !== null)
		setMessages(idUserSession, friendLogin);
	else
		document.getElementById("h3NomContact").innerHTML = "<b>"+"Select a contact..."+"</b>";
	setContact(idUserSession);
}

// Press enter will send a message, shit+enter will just go to next line
document.getElementById("champTexte").onkeyup = function(e){
	e = e || event;
	if (e.keyCode === 13 && !e.shiftKey) 
		sendMessageText();
	return true;
};

document.getElementById("subButton").addEventListener("click", function(){
	sendMessageText();
});

function sendMessageText() {
	var myChampTexte = document.getElementById("champTexte");
	var s = myChampTexte.value;
	if(s !== "") {
		if(friendLogin == undefined) {
			topBarChat("Select a contact please", true);
			$('document').ready(function() {
				$(window).scrollTop(0);
			});
			//myChampTexte.value = "";
			console.log("Pas de contact, choisir un contact avant de send un message");
			return;
		}
		myChampTexte.value = "";
		s = s.trim();
		sendMessageToServeur(idUserSession, friendLogin, s);
		refreshPage();
	}
}

function sendMessageToServeur(id_user_session, pseudo_receiver, message) {
	$.ajax({
		url : "../sendmessage?",
		type : "POST",
		data : "id_session=" + id_user_session + "&pseudo_receiver="+pseudo_receiver+"&message="+message,
		dataType : "json",
		success : function(rep) {
			refreshPage(); // ADD MAYBE BUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "sendMessageToServeur");
		}
	});
}

/* Gestion du bloc de contacts */
function setContact(id_user_session) {
	$.ajax({
		url : "../seencontact?",
		type : "GET",
		data : "id_session=" + id_user_session,
		dataType : "json",
		success : function(rep) {
			responseSetContact(rep, id_user_session);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "setContact");
		}
	});
}

function responseSetContact(rep, id_user_session) {
	var myListeContact = document.getElementById("idListeContact");
	while (myListeContact.hasChildNodes()) 
		myListeContact.removeChild(myListeContact.lastChild);

	for(var i=0 ; i<rep.length ; i++) {
		var loginFriend = rep[i].loginFriend;
		var isConnected = rep[i].connected;
		var nbMessageNotRead = rep[i].nbMessageNotRead;
		var newBaliseA = document.createElement("a");
		newBaliseA.href = "chat.html?friend_login="+loginFriend;
		newBaliseA.className = "list-group-item";
		var myColor = (isConnected === "1") ? "green" : "#428bca";
		var myColorNum = (nbMessageNotRead === "0") ? myColor : "white";
		newBaliseA.innerHTML = loginFriend+"<span class=\"badge\" style=\"color:"+myColorNum+";background-color:"+myColor+"\">"+nbMessageNotRead+"</span>";
		var newBaliseLi = document.createElement("li");
		newBaliseLi.className = "list-group-item";
		newBaliseLi.appendChild(newBaliseA);
		myListeContact.appendChild(newBaliseLi);
		console.log("Contact "+loginFriend+" lastDate = "+rep[i].dateLastMessage);
	}
}

/* Gestion du bloc de messages */
function setMessages(id_user_session, friend_login) {
	$.ajax({
		url : "../seenmessage?",
		type : "GET",
		data : "id_session=" + id_user_session + "&pseudo_other=" + friend_login,
		dataType : "json",
		success : function(rep) {
			if(rep.erreur2 != undefined) {
				console.log("Contact does not exist");
				topBarChat("Contact does not exist", true);
				//setTimeout(function(){ window.location.href = "chat.html?" }, 2000);
			}
			else {
				var obj = document.getElementById("idDivMessages");
				var needToMajScroll = (obj.scrollTop === obj.scrollHeight-heightIdDivMessage) ? true : false;
				console.log("NeedToMajScroll = "+needToMajScroll);
				responseSetMessages(rep, friend_login);
				if(needToMajScroll)
					setScrollbar();
			}
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur, "setMessages");
		}
	});
}




function responseSetMessages(rep, pseudo_friend) {

	document.getElementById("h3NomContact").innerHTML = "<b>"+pseudo_friend+"</b>";
	var myDiv = document.getElementById("idDivMessages");
	while (myDiv.hasChildNodes()) // Remove l'ancien affichage
		myDiv.removeChild(myDiv.lastChild);
	var lastVu = null;
	for(var i=0 ; i<rep.length ; i++) {
		var login = rep[i].login;
		var message = rep[i].message;
		var date = rep[i].date_send;
		var newBalise = document.createElement("p");
		var num = (login === pseudo_friend) ? 2 : 1;
		if(pseudo_friend === login)
			num = 1;
		var isRead = (login !== pseudo_friend) ? rep[i].isRead : null;
		newBalise.innerHTML = message;
		newBalise.className ="m m"+num;
		if(isRead !== null && isRead === "1") {
			lastVu = newBalise;
			dateVu = date; // SERT A RIEN A supp je crois
		}
		else if(isRead === null) {
			lastVu = null;
		}
		myDiv.appendChild(newBalise);
	}
	if(lastVu !== null)
		lastVu.innerHTML += " <i style=\"font-size:10px;color:black\">[seen]</i>";
}

function errorFunction(resultatXHR, statut, erreur, fctName) {
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Maybe user is not connected.");
	console.log("Fct ==> "+fctName);
	console.log("statut ==> "+statut);
}

function get_ParamGET(param) {
	var vars = {};
	window.location.href.replace( location.hash, '' ).replace( 
		/[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
		function( m, key, value ) { // callback
			vars[key] = value !== undefined ? value : '';
		});
	if ( param )
		return vars[param] ? vars[param] : null;	
	return vars;
}

/* Auto-completion pour la recherche d'utilisateur */
$('#idSearchLogin').autocomplete({
	source: function(requete, reponse){ 
		$.ajax({
			url : "../getloginbeginbyservlet?",
			type : "GET",
			data : "begin_by=" + $('#idSearchLogin').val(),
			dataType : "json",
			success : function(rep) {
				reponse(rep);
			}, 
			error : function(resultatXHR, statut, erreur) {
				errorFunction(resultatXHR, statut, erreur, "propositionContact");
			}
		});
	},
	minLength: 1
});


function goToSearchContact() {
	var t = document.getElementById("idSearchLogin");
	window.location.href = "chat.html?friend_login="+t.value;
}

document.getElementById("idButtonSearchLogin").addEventListener("click", function(){
	goToSearchContact();
});

document.getElementById("idSearchLogin").onkeyup = function(e){
	e = e || event;
	if (e.keyCode === 13) 
		goToSearchContact();
	return true;
};



// fr/chat.html?friend_login=log2


function topBarChat(message, err) {
	if(err)
		$("<div />", { class: 'erreur_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
	else
		$("<div />", { class: 'ok_topbar', text: message }).hide().prependTo("body")
	.slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
}
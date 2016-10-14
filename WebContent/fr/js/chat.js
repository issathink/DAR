var userLogin = get_ParamGET("user_login");
var friendLogin = get_ParamGET("friend_login");

if(userLogin === "" || friendLogin === "") 
	alert("Error : 'user_login' = "+userLogin+", friend_login = "+friendLogin);

refreshPage();
var obj = document.getElementById("idDivMessages"); ///////////////////////////////////::
obj.scrollTop = obj.scrollHeight;



// Press enter will send a message, shit+enter will just go to next line
document.getElementById("champTexte").onkeyup = function(e){
	e = e || event;
	if (e.keyCode === 13 && !e.shiftKey) 
		sendMessageText();
	return true;
};


function refreshPage() {
	setMessages(userLogin, friendLogin);
	setContact(userLogin);
	setScrollbar();
}


document.getElementById("subButton").addEventListener("click", function(){
	sendMessageText();
});


function sendMessageText() {
	var myChampTexte = document.getElementById("champTexte");
	var s = myChampTexte.value;
	if(s !== "") {
		myChampTexte.value = "";
		sendMessageToServeur(userLogin, friendLogin, s);
		refreshPage();
	}
}

function sendMessageToServeur(pseudo_sender, pseudo_receiver, message) {
	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/sendmessage?",
		type : "POST",
		data : "pseudo_sender=" + pseudo_sender + "&pseudo_receiver="+pseudo_receiver+"&message="+message,
		dataType : "json",
		success : function(rep) {
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur);
		}
	});
}


function setContact(user_login) {
	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/seencontact?",
		type : "GET",
		data : "pseudo_user=" + user_login,
		dataType : "json",
		success : function(rep) {
			responseSetContact(rep, user_login);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur);
		}
	});
}

function responseSetContact(rep, user_login) {
	var myListeContact = document.getElementById("idListeContact");
	while (myListeContact.hasChildNodes()) 
		myListeContact.removeChild(myListeContact.lastChild);

	for(var i=0 ; i<rep.length ; i++) {
		var loginFriend = rep[i].loginFriend;
    	//alert("Friend = "+loginFriend);

    	var newBaliseA = document.createElement("a");
    	newBaliseA.href = "chat.html?user_login="+user_login+"&friend_login="+loginFriend;
    	newBaliseA.className = "list-group-item";
    	newBaliseA.innerHTML = loginFriend+"<span class=\"badge\">3</span>";

    	var newBaliseLi = document.createElement("li");
    	newBaliseLi.className = "list-group-item";
    	newBaliseLi.appendChild(newBaliseA);


    	myListeContact.appendChild(newBaliseLi);
    }
}


function setMessages(user_login, friend_login) {
	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/seenmessage?",
		type : "GET",
		data : "pseudo_user=" + user_login + "&pseudo_other=" + friend_login,
		dataType : "json",
		success : function(rep) {
			responseSetMessages(rep, friend_login);
		}, 
		error : function(resultatXHR, statut, erreur) {
			errorFunction(resultatXHR, statut, erreur);
		}
	});
}


function responseSetMessages(rep, pseudo_friend) {
	document.getElementById("h3NomContact").innerHTML = "<b>"+pseudo_friend+"</b>";
	
	var myDiv = document.getElementById("idDivMessages");


	while (myDiv.hasChildNodes()) 
		myDiv.removeChild(myDiv.lastChild);

	for(var i=0 ; i<rep.length ; i++) {
		var login = rep[i].login;
		var message = rep[i].message;
		var date = rep[i].date_send;

		var newBalise = document.createElement("p");
		newBalise.innerHTML = message;
		var num;

		if(login === pseudo_friend) 
			num = 2;
		else 
			num = 1;
		newBalise.className ="m m"+num;
		myDiv.appendChild(newBalise);
	}



	
	
}


function setScrollbar() {
	var obj = document.getElementById("idDivMessages");
	obj.scrollTop = obj.scrollHeight + 10;
}


function errorFunction(resultatXHR, statut, erreur) {
	alert("En erreur : "+erreur);
	alert("XHR = "+resultatXHR.responseText);
	alert("Statut = "+ statut);
	console.log("Error(" + status + ") : " + resultatXHR.responseText);
	console.log("Maybe user is not connected.");
	// window.location.href = "login.html";
}

function get_ParamGET(param) {
	var vars = {};
	window.location.href.replace( location.hash, '' ).replace( 
		/[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
		function( m, key, value ) { // callback
			vars[key] = value !== undefined ? value : '';
		}
		);

	if ( param ) {
		return vars[param] ? vars[param] : null;	
	}
	return vars;
}
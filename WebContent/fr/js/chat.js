setScrollbar();
setMessages();

function setMessages() {
	var id_user = 1;
	var id_other = 2;

	// alert("1");
	// alert("2");


	// alert("3");

	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/seenmessage?",
		type : "GET",
		// crossDomain: true,
		data : "id_user=" + id_user + "&id_other="+id_other,
		dataType : "json",
		//callback : responseSetMessages,
		success : function(rep) {
            responseSetMessages(rep);
        }, //function(repp, statut) {
		// 	var rep = JSON.stringify(repp);
		// 	alert("Genial("+statut+") = "+rep.length);
		// 	for (var i=1; i<=rep.length; i++) {
		// 		var tmp = "Message_"+i;
		// 		var message = rep[i]
		// 		alert(message);
		// 	}
		// 	responseSetMessages(rep);
		// },
		error : function(resultatXHR, statut, erreur) {
			alert("En erreur : "+erreur);
			alert("XHR = "+resultatXHR.responseText);
			alert("Statut = "+ statut);
			console.log("Error(" + status + ") : " + resultatXHR.responseText);
			console.log("Maybe user is not connected.");
			//window.location.href = "login.html";
		}
	});

	// alert("4");
}


function responseSetMessages(rep) {
	// Faut renvoyer une liste ou tableaux de messages : ["message1": "contenu", ...]
	// et pas {"message1": "contenu", ...}
	
	console.log(rep.Message_6);
	console.log(rep.Message_5);
	console.log(rep.length); // Regarde la console ca affiche undefined
	
	// Vu qu'on connait pas la taille on peut pas faire une boucle pour afficher tous les messages
}


function setScrollbar() {
	var obj = document.getElementById("affMessages");
	obj.scrollTop = obj.scrollHeight;
}

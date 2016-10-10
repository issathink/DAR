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
		data : "id_user=" + id_user + "&id_other="+id_other,
		dataType : "json",
		success : function(rep) {
			responseSetMessages(rep);
		}, 
		error : function(resultatXHR, statut, erreur) {
			alert("En erreur : "+erreur);
			alert("XHR = "+resultatXHR.responseText);
			alert("Statut = "+ statut);
			console.log("Error(" + status + ") : " + resultatXHR.responseText);
			console.log("Maybe user is not connected.");
			window.location.href = "login.html";
		}
	});

	// alert("4");
}


function responseSetMessages(rep) {

	
	for(var i=0 ; i<rep.length ; i++) {
		var login = rep[i].login;
		var message = rep[i].message;
		var date = rep[i].date_send;
		//alert(login+" | "+message+" | "+date);
		var myDiv = document.getElementById("idDivMessages");
		var newBalise = document.createElement("p");
		newBalise.innerHTML = message;
		var num;
		if(i % 2 == 0)
			num = 1;
		else
			num = 2;
		newBalise.className ="m m"+num;
		myDiv.appendChild(newBalise);
	}

		

	
	
}


function setScrollbar() {
	var obj = document.getElementById("affMessages");
	obj.scrollTop = obj.scrollHeight;
}

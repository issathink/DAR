setScrollbar();
setMessages();

function setMessages() {
	var id_user = 1;
	var id_other = 2;

	alert("1");
	alert("2");


	alert("3");

	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/seenmessage?",
		type : "GET",
		// crossDomain: true,
		data : "id_user=" + id_user + "&id_other="+id_other,
		dataType : "json",
		//callback : responseSetMessages,
		success : responseSetMessages,//function(repp, statut) {
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

	alert("4");
}

function responseSetMessages(rep2) {
	alert("REP2("+rep2.length+") = "+ typeof(rep2));
	var rep = eval('(' + rep2 + ')'); 
	//rep = JSON.parse(rep2);

	alert("SUCCESS BIEN = "+typeof(rep));

	alert("Size = "+rep.length);

	for (var i=1; i<=rep.length; i++) {
		var tmp = "Message_"+i;
		var message = rep[i];
		alert(message);
	}
		//$('#elems').prepend(data).fadeIn("slow");
	}		

	function setScrollbar() {
		var obj = document.getElementById("affMessages");
		obj.scrollTop = obj.scrollHeight;
	}


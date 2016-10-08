setScrollbar();
setMessages();

function setMessages() {
	var id_user = 1;
	var id_other = 2;

	alert("1");
	alert("2");
	query = document.forms["s_form"]["search"].value;
	alert("31");
	if(query == "") {
		console.log("Empty query");
		return;
	}

	alert("3");

	$.ajax({
		url : "http://vps197081.ovh.net:8080/FindYourFlat/seenmessage?id_user=1&id_other=2",
		type : "get",
		crossDomain: true,
		data : "format=jsonp" + "&id_user=" + id_user + "&id_other="+id_other,
		dataType : "jsonp",
		callback : responseSetMessages,
		success : function(rep) {
			responseSetMessages(rep);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
			console.log("Maybe user is not connected.");
			window.location.href = "login.html";
		}
	});

	alert("3");
}

function responseSetMessages(rep) {
	if(rep.length == 0) {
		topBar("Votre requete n'a pas retourne de resultats.");
		return;
	}

	for (var i=1; i<=rep.length; i++) {
		var tmp = "Message_"+i;
		var message = rep[i]
		alert(message);
	}
	$('#elems').prepend(data).fadeIn("slow");
}

function setScrollbar() {
	var obj = document.getElementById("affMessages");
	obj.scrollTop = obj.scrollHeight;
}


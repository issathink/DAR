function responseIsConnectedHeader(response) {
	console.log("responseIsConnectedChat : Response = "+response);
	if(response.ok != undefined) {
		console.log("already connected");
		boolIsConnected = true;
		document.getElementById("top_button").innerHTML = "<button onclick='goToChat()' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-envelope' aria-hidden='true'></a> </button>" +
		"<button onclick='toInfos()' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>" +
		"<button onclick='disconnect();' type='button' class='btn btn-default btn-md'>" +
		"<a class='glyphicon glyphicon-log-out' aria-hidden='true'></a> </button>";

	} else {
		document.getElementById("top_button").innerHTML = "<div class='depl_haut'> <a href='signin.html'>Se connecter</a></div>";
		boolIsConnected = false;
	}
}


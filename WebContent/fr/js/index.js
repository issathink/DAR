isConnected(responseIsConnected);


function responseIsConnected(response) {

	if(response.ok != undefined) {
		// alert("Already logged in: " + getCookie(C_NAME));
		document.getElementById("index_signin").innerHTML = "<button type='button'" 
			+ " class='btn btn-default btn-md'><a class='glyphicon glyphicon-envelope'" 
			+ " aria-hidden='true' href='chat.html'></a></button>";

		document.getElementById("index_signup").innerHTML = "<button type='button'" 
			+ " class='btn btn-default btn-md' href='profile.html'>"
			+ "<a class='glyphicon glyphicon-user' aria-hidden='true'></a></button>";

		document.getElementById("index_logout").innerHTML = "<button onclick='disconnect();' "+
		"type='button' class='btn btn-default btn-md'>"+
		"<a class='glyphicon glyphicon-log-out' aria-hidden='true'></a> </button>";

	} else {
		setCookie(C_NAME, "-1", 1);
		console.log("responseIsConnected: session expired");
	}

}


function changePage() {
	addr = document.getElementById("searchTextField").value;
	if(addr.length > 0)
		window.location.href = "home.html?adresse=" + addr;
}


isConnected(responseIsConnected);


function responseIsConnected(response) {

    if(response.ok != undefined) {
        // alert("Already logged in: " + getCookie(C_NAME));
        document.getElementById("index_signin_message").innerHTML = "<button type='button'" 
            + " class='btn btn-default btn-md'><a class='glyphicon glyphicon-envelope'" 
            + " aria-hidden='true' href='chat.html'></a></button>";
        
        document.getElementById("index_signup_profile").innerHTML = "<button type='button'" 
            + " class='btn btn-default btn-md' href='profile.html'>"
            + "<a class='glyphicon glyphicon-user' aria-hidden='true'></a></button>";
    } else {
        setCookie(C_NAME, "-1", 1);
        console.log("responseIsConnected: session expired");
    }
 
}


function changePage() {
	addr = document.getElementById("getAdresse").value;
	if(addr.length > 0)
		window.location.href = "home.html?adresse=" + addr;
}


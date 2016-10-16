isConnected(responseIsConnected);


function responseIsConnected(response) {
	console.log(response);
	if(response.ok != undefined) {
		console.log("already connected");
		
		document.getElementById("top_button").innerHTML = "<button type='button' class='btn btn-default btn-md'>" +
			"<a class='glyphicon glyphicon-envelope' aria-hidden='true'></a> </button>" +
			"<button type='button' class='btn btn-default btn-md'>" +
			"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>";
		
		document.getElementById("connected").innerHTML = "<div class='input-group'> <input type='text' class='form-control'" +
		"placeholder='Type your message ...'> <span class='input-group-btn'>" +
		"<button class='btn btn-default' type='button'>Envoyer</button> </span> </div>";
		
		document.getElementById("donner_note").innerHTML = "<div id='note'>Donner une note :</div>";
		document.getElementById("etoiles").innerHTML = "<div class='stars' id='rateYo'></div> <script type='text/javascript'>" +
												"$(function() { $('#rateYo').rateYo({ rating : '50%', spacing : '10px', }); }); </script>";
	} else {
		console.log("Noppppppp!");
		/*document.getElementById("top_button").innerHTML = "<button type='button' class='btn btn-default btn-md' disabled>" +
							"<a class='glyphicon glyphicon-envelope' aria-hidden='true'></a> </button>" +
							"<button type='button' class='btn btn-default btn-md' disabled>" +
							"<a class='glyphicon glyphicon-user' aria-hidden='true'></a> </button>";*/
		document.getElementById("top_button").innerHTML = "<div class='depl_haut'> <a href='signin.html'>Se connecter</a></div>";
	}
}
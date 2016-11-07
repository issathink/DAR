var C_NAME   = "fyf_ident";
var MAPS_KEY = "AIzaSyDZv1TYlMIMxAPIV1ZuspwPD5zZEjylW28"

/************************ Cookie mnam mnam mnam ***********************/
function setCookie(cname, cvalue, minutes) {

    var d = new Date();
    d.setTime(d.getTime() + (minutes * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();

    document.cookie = cname + "=" + cvalue + "; " + expires;
    console.log("[SetCookie] OK " + cname + ", " + cvalue + ", " + minutes);
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');

    for ( var i = 0; i < ca.length; i++) {
        var c = ca[i];

        while (c.charAt(0) == ' ')
            c = c.substring(1);

        if (c.indexOf(name) == 0) {
            str = c.substring(name.length, c.length);
            if(str == "-1") {
                console.log("Oh oh it has been reinitialised.");
                return null;
            }
            console.log("[GetCookie] OK " + cname + ": " + str);
            return str;
        }
    }

    console.log("[GetCookie] Nothing to show");
    return null;
}


function checkCookie(name) {
    us = getCookie(name);
    if (us != null) {
        // alert("Welcome again " + us);
    } else {
        console.log("[CheckCookie] " + name + " FAILED! ");
    }
}


function isConnected(callBack) {

    genId = getCookie(C_NAME);
    if(genId == null) {
        console.log("No previous session id.");
        callBack({});
        return;
    }

    $.ajax({
        url : "http://vps197081.ovh.net:8080/Issa/isconnected?",
        type : "get",
        crossDomain: false,
        data : "format=json" + "&session_id=" + genId,
        dataType : "json",
        success : function(rep) {
            callBack(rep);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            // We do nothing if there isn't an active session
            console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
            console.log("Maybe user is not connected.");
            callBack({});        
        }
    });
}


/* Convetion : Cookie value equals "-1" means there is no active session. */
function logout() {
    setCookie(C_NAME, "-1", 1);
    window.location.href = "index.html";
}


/* Display banner messages */
function topBar(message) {
    $("<div />", { class: 'erreur_topbar', text: message }).hide().prependTo("body")
    .slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
}


function okBar(message) {
    $("<div />", { class: 'ok_topbar', text: message }).hide().prependTo("body")
    .slideDown('fast').delay(5000).fadeOut(function() { $(this).remove(); });
}


function validateEmail(email) {
    var regexp = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regexp.test(email);
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


function adressToLatLng(adress) {
    var req = 

    $.ajax({
        url : "http://maps.googleapis.com/maps/api/geocode/json?",
        type : "GET",
        data : "address="+adress+"&key="+MAPS_KEY;
        dataType : "json",
        success : function(rep) {
            responseSetAPI(rep, adresse);
        }, 
        error : function(resultatXHR, statut, erreur) {
            errorFunction(resultatXHR, statut, erreur, "setTransport");
        }
    });
}

function escapeAdress(adress) {

}
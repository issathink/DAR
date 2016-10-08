var C_NAME   = "fyf_ident";

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


function isConnected() {

    genId = getCookie(C_NAME);
    if(genId == null) {
        console.log("No previous session id.");
        responseIsConnected({"ok": 1});
        return;
    }

    $.ajax({
        url : "http://vps197081.ovh.net:8080/FindYourFlat/isconnected?",
        type : "get",
        crossDomain: true,
        data : "format=jsonp" + "&session_id=" + genId,
        dataType : "jsonp",
        callback : responseIsConnected,
        success : function(rep) {
            responseIsConnected(rep);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            // We do nothing if there isn't an active session
            console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
            console.log("Maybe user is not connected.");
            // window.location.href = "login.html";
        }
    });
}


function responseIsConnected(rep) {

   if(rep.ok != undefined) {
        alert("Already logged in: " + getCookie(C_NAME));
        console.log("Elem: " + document.getElementById("popopo"));
        document.getElementById("index_signin_message").innerHTML = "<button type='button'" 
            + " class='btn btn-default btn-md'><a class='glyphicon glyphicon-envelope'" 
            + " aria-hidden='true' href='chat.html'></a></button>";
        
        document.getElementById("index_signup_profile").innerHTML = "<button type='button'" 
            + " class='btn btn-default btn-md' href='profile.html'>"
            + "<a class='glyphicon glyphicon-user' aria-hidden='true'></a></button>";
   } else {
        setCookie(C_NAME, "-1", 1);
        alert("responseIsConnected: session expired");
   }
    
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
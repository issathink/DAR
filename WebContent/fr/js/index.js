loadData();

function loadData() {

    alert("Ayyyy");
    genId = getCookie(C_NAME);
    if(genId == null) {
        console.log("No previous session id.");
        window.location.href = "login.html";
    }

    $.ajax({
        url : "http://vps197081.ovh.net:8080/FindYourFlat/isconnected?",
        type : "get",
        crossDomain: true,
        data : "format=jsonp" + "&ident=" + genId + "&is_ext=yes",
        dataType : "jsonp",
        callback : responseLoadData,
        success : function(rep) {
            responseLoadData(rep);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
            console.log("Maybe user is not connected.");
            window.location.href = "login.html";
        }
    });
}

function responseLoadData(rep) {

    data = "";

    if (rep.ret_code != undefined) {
        if (rep.ret_code == 0) {
            new User(rep.login);
            for(var i=0; i<10; i++) {
                v = i + 1;
                index = "filtre" + v;
                el = new Elem(parseInt(rep[index], 10), rep["n"+v], rep["d"+v]);
                if (parseInt(rep[index], 10) > 0)
                    nb_elems++;
                // filters[i] = parseInt(rep[index], 10);
                // console.log("filters[" + i + "] = " + JSON.stringify(elems[i]));
                data += el.getHtml();
                showFilters();
            }
        } else {
            console.log("Not connected or session expired.");
            window.location.href = "login.html";
        }
    } else {
        console.log("Oops something went wrong.");
        window.location.href = "login.html";
    }

    $('#elems').prepend(data).fadeIn("slow");
    
}

function showFilters() {

}


function search() {

    query = document.forms["s_form"]["search"].value;
    if(query == "") {
        console.log("Empty query");
        return;
    }

    $.ajax({
        url : "http://vps197081.ovh.net:8080/FindYourFlat/search?",
        type : "get",
        crossDomain: true,
        data : "format=jsonp" + "&q=" + query,
        dataType : "jsonp",
        callback : responseSearch,
        success : function(rep) {
            responseSearch(rep);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
            console.log("Maybe user is not connected.");
            window.location.href = "login.html";
        }
    });

}

function responseSearch(rep) {

    data = "";
    $("#elems").text("");

    if(rep.length == 0) {
        topBar("Votre requete n'a pas retourne de resultats.");
        return;
    }

    for (var i=0; i<rep.length; i++) {
        s_result[i] = new Elem(rep[i].id, rep[i].name, rep[i].desc, "s");
        data += s_result[i].getHtml();
        // console.log(JSON.stringify(rep[i]));
    }
    $('#elems').prepend(data).fadeIn("slow");

}


function delFilter(id) {
    if (id < 0) {
        console.log("Why why id: " + id + " ???");
        return;
    }
    console.log("Je vais del : id=" + id);
    found = false;

    for (var i=0; i<10; i++) {
        if (elems[i].id == id) {
            console.log("Bingo id: " + id + " n: " + elems[i].name + " d: " + elems[i].desc);
            elems[i].id = -1;
            nb_elems--;
            found = true;
            break;
        }
    }
    
    if (!found)
        console.log("Why there isn't id: " + id);
    else
        updateFilter();

}

function addFilter(id) {

    if (nb_elems >= 10) {
        console.log("Sorry, you already have 10 filters. id: " + id);
        return;
    }

    found = false;
    console.log("Avant nb_elems : " + nb_elems);
    
    for (var i=0; i<10; i++) {
        if (elems[i].id == -1) {
            console.log("Trouve case vide : " + i);
            elems[i].id = id;
            nb_elems++;
            found = true;
            break;
        }
    }

    console.log("Apres nb_elems : " + nb_elems);
    if (!found)     
        alert("J'ai pas trouve de case vide (id: " + id + ")");
    else
        updateFilter();

}

function updateFilter() {

    data = "&";
    for (var i=0; i<9; i++)
        data += "filtre" + (i+1) + "=" + elems[i].id + "&";
    data += "filter" + (i+1) + "=" + elems[i].id;
    console.log(data);
    console.log("indent : " + genId);

    $.ajax({
        url : "http://vps197081.ovh.net/api/update_user_filters?",
        type : "get",
        crossDomain: true,
        data : "format=jsonp" + "&ident=" + genId + "&is_ext=yes" + data,
        dataType : "jsonp",
        callback : responseFilter,
        success : function(rep) {
            responseFilter(rep);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
            console.log("Maybe user is not connected.");
            // window.location.href = "login.html";
            topBar("Unexpected error (make sure you're connected)");
        }
    });

}

function responseFilter(rep) {

    if(rep.ret_code == 0) {

        okBar(rep.message);
        setTimeout(function() {
            window.location.href = "acc.html";
        }, 2000);

    } else {

        console.log("What went wrong ??????");
        topBar("Unexpected error.");

    }
}



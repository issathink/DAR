// var filter_cpt = 0;
// var filters = [];
// var user = null;

var elems    = []; /* Array of id (filter id) [size = 10] */
var s_result = [];
var elem_cpt = 0;
var genId    = null;
var user	 = null;
var nb_elems = 0;

function main() {

	// Load the cookies and check if user is already connected
	if ((ident = getCookie(C_NAME)) != null) {
		console.log("User already connected.");

		$.ajax({
			url : "http://vps197081.ovh.net:8080/FindYourFlat/",
			type : "get",
			crossDomain: true,
			data : "format=jsonp" + "&ident=" + ident,
			dataType : "jsonp",
			callback : responseMain,
			success : function(rep) {
				responseMain(rep);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log("Error(" + textStatus + ") : " + jqXHR.responseText);
			}
		});
	}
}

function responseMain(rep) {
	if(rep.ret_code == 0) {
		console.log("Recently logged in : " + rep.message);
		window.location.href = "acc.html"
	} else {
		console.log("Unexcped error : " + rep.message);
	}
}

function func_erreur(jqXHR, textStatus, errorThrown) {
	console.log("Error: " + errorThrown);
	document.body.className = '';
}

function User(login) {

	this.login = login;
	user = this;

}

function validateEmail(email) {
	var regexp = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return regexp.test(email);
}

function Elem(id, name, desc, type) {
	this.id = id;
	this.name = name;
	this.desc = desc;
	if(type == undefined) {
		console.log("Mein filters");
		elems[elem_cpt++] = this;
	}
}

function isInMyElems(id) {
	for(var i=0; i<10; i++)
		if(elems[i] != undefined && elems[i].id == id)
			return true;
	return false;
}

Elem.prototype.getHtml = function() {
	
	elem = "elem_add";
	addOrDel = "onclick=\"addFilter('" + this.id + "'); return false;\">Add";
	if(isInMyElems(this.id)) {
		elem = "elem_del";
		addOrDel = "onclick=\"delFilter('" + this.id + "'); return false;\">Delete";
	}


	if(this.id == -1)
		return "<div class='elem_ col-xs-12'><p>No filter (use the search bar to find one)</p></div>"

	return "<div class='elem_ col-xs-12'>"
        + "<a class='elem_title' href='#'>#" + this.name + "</a>"
        + "<a class='" + elem + " btn-normal' href='#' " + addOrDel + "</a>"
        + "<p class='elem_description'>"
        + this.desc + "</p><hr /></div>"; 
};

function Filter(id, user, texte, date, score, liked) {

	this.id=id;
	this.filterType=filterType;
	this.url=url;
	this.nbSecStart=nbSecStart;
	this.nbSecEnd=nbSecEnd;
	this.liked=liked;
	this.user=user;

	this.num=cpt;
	filters[filter_cpt]=this;
	filter_cpt++;
	
}

Filter.prototype.getHtml = function(com) {

	img = "src='img/ic_like.png'";
	if(this.liked == 1)
		img = "src='img/ic_liked.png'";

	var s = "<div class='commentaire' id='comment_" + this.id + "'><div class='commentaire_text'>"
			+ this.texte + "</div><div class='commentaire_info'><span class='commentaire_nb_like'>" + this.score 
			+ "</span><a href='#' onclick=\"likeCommentaire('" + this.num 
			+ "'); return false;\"><img id='img' " + img + " style='width:30px;height:30px;vertical-align:middle' />"
			+	"</a><span class='commentaire_date'>" + this.user.login + ", </span><span id='date'>" + this.date + "</span>";

	deb = "<a id='" + this.num 
		  + "' href='#' onclick=\"addRemoveFriend('" + this.num + "'); return false;\"><span class='commentaire_foll'>";
	fin = "</span></a></div></div>";

    if(env.id == this.user.id)
    	s += deb + "Posted by Me" + fin;
    else if(this.user.contact == true)
    	s += deb + "Unfollow " + this.user.login + fin;
    else
    	s += deb + "Follow " + this.user.login + fin;

    return s;

};



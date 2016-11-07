var imageSante = 'images/sante.png';
var imageSport = 'images/sport.png';
var imageSecurite = 'images/securite.png';
var imageEducation = 'images/education.png';
var imageTransport = 'images/transport.png';



function setMarkers(map, location, image) {
 
  var image = {
    url: image,
    // This marker is 20 pixels wide by 32 pixels high.
    size: new google.maps.Size(20, 32),
    // The origin for this image is (0, 0).
    origin: new google.maps.Point(0, 0),
    // The anchor for this image is the base of the flagpole at (0, 32).
    anchor: new google.maps.Point(0, 32)
  };
 

    var marker = new google.maps.Marker({
      position: location,
      map: map,
      icon: image
    });
  }
}
var roomID;
$(document).ready(function() {
    database = firebase.database();
    name = "user " + Math.floor(Math.random() * Math.floor(5));
    roomID = window.location.search.substring(1);
    firebase.database().ref('messages/' + roomID).on('child_added', function(snapshot) {
        var snap = snapshot.val();
        var html = "<li class='message' id='message-" + snapshot.key + "'>";
        html += snap.time + " " + snap.user + ": ";
        if (snap.type == "text") {
            html += snap.message;
        } else {
            html += "<a href=\"" + snap.message + "\"><img src=\"" + snap.message + "\" /></a>";
        }
        html += "</li>";
        document.getElementById("messages").innerHTML += html;
    });
});

function sendMessage() {
    var message = document.getElementById("message").value;
    var date = new Date();
    var time = hours_with_leading_zeroes(date) + ":" + minutes_with_leading_zeroes(date);
    firebase.database().ref('messages/' + roomID).push().set({
        type: "text",
        user: name,
        message: message,
        time: time
    }, function(error) {
        if (error) {
            console.log("Write failed");
        }
    });
    return false;
}

function fetchBlobstoreUrl() {
    fetch('/blobstore')
        .then((response) => {
            return response.text();
        })
        .then((imageUploadUrl) => {
            const messageForm = document.querySelector('#image-form');
            messageForm.action = imageUploadUrl;
            messageForm.classList.remove('hidden');
        });
}

function minutes_with_leading_zeroes(date) {
    return (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
}

function hours_with_leading_zeroes(date) {
    return (date.getHours() < 10 ? '0' : '') + date.getHours();
}

function getRoomDetails() {
    let roomDetails = {
        shopName: "McDonald's",
        postalCode: "123456",
        deliveryAddress: "1 Sorby Adams Drive",
        description: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
        category: "Food",
        deliveryFee: 20,
        minimumOrderPrice: 100,
        noOfPeopleInRoom: 3,
        total: 70,
    };
    let roomDetailsContainer = document.getElementById("room-details-container");
    let roomDetailsString = `
  <div>
  <span class="room-details-shopName">${roomDetails.shopName}</span>
  <br />
  <br />
  
  <span class="room-details-header">Delivering To:</span>
  <br />
  <span class="room-details-value">${roomDetails.deliveryAddress}</span>
  <br />
  <span class="room-details-value">${roomDetails.postalCode}</span>
  <br />
  <br />
  
  <span class="room-details-header">Delivery fee: </span>
  <br />
  <span class="room-details-value">$${roomDetails.deliveryFee}</span>
  <br/>
  
  <span class="room-details-header">$ left to minimum order: </span>
  <br />
  <span class="room-details-value">$${
    roomDetails.minimumOrderPrice - roomDetails.total
  }</span>
  </div>
  
  <div class="room-details-exit">
  <form action="/room" method="delete">
  <button type="submit" class="btn btn-danger">Delete & exit room</button>
  </form>
  </div>
  `;
    roomDetailsContainer.innerHTML = roomDetailsString;
}

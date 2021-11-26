jQuery(document).ready(function(){
    var $ = jQuery;
    var targetIdField = $("#targetId");
    var commandField = $("#command");
    var submitButton = $("#submit");
    var responceField = $("#responce");
    var errorField = $("#error");


    var ws = new WebSocket("ws://localhost:4000/test");
    ws.onmessage = function (evt) { 
        var received_msg = JSON.parse(evt.data);
        //console.log(received_msg);

        responceField.val("");
        errorField.val("");

        if(received_msg.type == "commandResponce"){
            var responceString = JSON.stringify(received_msg.responce);
            responceField.val(responceString);
        }
        else if(received_msg.type == "error"){
            var errorMessageString = JSON.stringify(received_msg.errorMessage);
            errorField.val(errorMessageString);
        }
        else{
            console.log("Unknown packet from pipe");
            console.log(received_msg);
        }

     };

     submitButton.click(function(){
         var targetId = parseInt(targetIdField.val());
         var command = commandField.val();
         var commandPacket = { type: "command", command: command, computerID: targetId};
         
         responceField.val("");
         errorField.val("");

         ws.send(JSON.stringify(commandPacket));
     });


});
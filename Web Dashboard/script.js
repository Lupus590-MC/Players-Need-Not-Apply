jQuery(document).ready(function(){
    var $ = jQuery;

    var tabs = $("#tabs");
    tabs.tabs();
    var newTabButton = $("#new-tab");
    var tabNumber = 3;
    newTabButton.click(function(){
        tabNumber+=1;
        tabs.children("ul").append("<li><a href=\"#tabs-"+tabNumber+"\">Aenean lacinia</a></li>");
        tabs.append("<div id=\"tabs-"+tabNumber+"\">"+"<p>Mauris eleifend est et turpis. Duis id erat. Suspendisse potenti. Aliquam vulputate, pede vel vehicula accumsan, mi neque rutrum erat, eu congue orci lorem eget lorem. Vestibulum non ante. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce sodales. Quisque eu urna vel enim commodo pellentesque. Praesent eu risus hendrerit ligula tempus pretium. Curabitur lorem enim, pretium nec, feugiat nec, luctus a, lacus.</p>"+"<p>Duis cursus. Maecenas ligula eros, blandit nec, pharetra at, semper at, magna. Nullam ac lacus. Nulla facilisi. Praesent viverra justo vitae neque. Praesent blandit adipiscing velit. Suspendisse potenti. Donec mattis, pede vel pharetra blandit, magna ligula faucibus eros, id euismod lacus dolor eget odio. Nam scelerisque. Donec non libero sed nulla mattis commodo. Ut sagittis. Donec nisi lectus, feugiat porttitor, tempor ac, tempor vitae, pede. Aenean vehicula velit eu tellus interdum rutrum. Maecenas commodo. Pellentesque nec elit. Fusce in lacus. Vivamus a libero vitae lectus hendrerit hendrerit.</p>"+"</div>");
        tabs.tabs("refresh");
    });

    var removeTabButton = $("#remove-tab");
    removeTabButton.click(function(){
        if(tabNumber>1){
            tabNumber-=1;
            tabs.children("ul").children()[tabNumber].remove();
            tabs.children()[tabNumber+1].remove();
            tabs.tabs("refresh");
        }
    });


    var targetIdField = $("#targetId");
    var commandField = $("#command");
    var submitButton = $("#submit");
    var responceField = $("#responce");
    var errorField = $("#error");
    var statusFields = {};

    var urlParams = new URLSearchParams(window.location.search);
    var webSocketName = urlParams.get('ws') || "test";
    //console.log(webSocketName);


    var ws = new WebSocket("ws://localhost:4000/"+webSocketName);
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

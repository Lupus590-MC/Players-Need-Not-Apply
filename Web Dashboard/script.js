jQuery(document).ready(function(){
    var $ = jQuery;

    var tabs = $("#tabs");
    tabs.tabs();

    /**
     * create a new tab for the new computer and set it up
     * @param  {number} ccComputerID
     * @param  {string} [ccComputerLabel]
     */
    function onNewConnection(ccComputerID, ccComputerLabel) {
        if(ccComputerLabel === undefined || ccComputerLabel === null || ccComputerLabel === ""){
            ccComputerLabel = "";
        }else{
            ccComputerLabel = ccComputerLabel + " - ";
        }

        tabs.children("ul").append("<li><a href=\"#computer-"+ccComputerID+"\">"+ccComputerLabel+"#"+ccComputerID+"</a></li>");
        var clonedTemplate = $("#template").clone();
        clonedTemplate.attr("id", "computer-"+ccComputerID);
        clonedTemplate.appendTo(tabs);
        tabs.tabs("refresh");
    }


    var newTabButton = $("#new-tab");
    var tabNumber = 0;
    newTabButton.click(function(){
        tabNumber+=1;
        onNewConnection(tabNumber);

        tabNumber+=1;
        onNewConnection(tabNumber, "test");
    });


    //why did I add ways to remove tabs, disconnects are probably only a sign that something is wrong?
    var removeTabButton = $("#remove-tab");
    removeTabButton.click(function(){
        if(tabNumber>0){
            tabNumber-=1;
            tabs.children("ul").children()[tabNumber].remove();
            tabs.children()[tabNumber+1].remove();
            tabs.tabs("refresh");
        }
    });

    var removeSpecificTabButton = $("#remove-specific-tab");
    var specificTabToRemove = $("#specific-tab-to-remove");
    removeSpecificTabButton.click(function(){
        if(tabNumber>0 && specificTabToRemove.val() !== ""){
            tabs.children("ul").children().children().each(
                function(index , tab){
                    if(tab.text === specificTabToRemove.val()){
                        tabNumber-=1;
                        tabs.children("ul").children()[index].remove();
                        tabs.children()[index+1].remove();
                        tabs.tabs("refresh");
                    }
                }
            );
        }
    });


    //TODO: rewrite to handle the tab system
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


     // TODO: rewrite https://stackoverflow.com/questions/17451660/one-click-event-handler-for-multiple-buttons
     submitButton.click(function(){
         var targetId = parseInt(targetIdField.val());
         var command = commandField.val();
         var commandPacket = { type: "command", command: command, computerID: targetId};

         responceField.val("");
         errorField.val("");

         ws.send(JSON.stringify(commandPacket));
     });


});

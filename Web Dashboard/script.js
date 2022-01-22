jQuery(document).ready(function(){
    let $ = jQuery;

    let tabs = $("#tabs");
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
        let clonedTemplate = $("#template").clone();
        clonedTemplate.attr("id", "computer-"+ccComputerID);
        clonedTemplate.appendTo(tabs);
        tabs.tabs("refresh");
    }

    let urlParams = new URLSearchParams(window.location.search);
    let webSocketName = urlParams.get('ws') || "test";

    //TODO: maybe use alert boxes for when the REPL errors?
    //TODO: rewrite to handle the tab system
    let targetIdField = $("#targetId");
    let commandField = $("#command");
    let submitButton = $("#submit");
    let responceField = $("#responce");
    let errorField = $("#error");
    let statusFields = {};


    let ws = new WebSocket("ws://localhost:4000/"+webSocketName);
    ws.onmessage = function (evt) {
        let received_msg = JSON.parse(evt.data);
        //console.log(received_msg);

        responceField.val("");
        errorField.val("");

        if(received_msg.type == "commandResponce"){
            let responceString = JSON.stringify(received_msg.responce);
            responceField.val(responceString);
        }
        else if(received_msg.type == "error"){
            let errorMessageString = JSON.stringify(received_msg.errorMessage);
            errorField.val(errorMessageString);
        }
        else{
            console.log("Unknown packet from pipe");
            console.log(received_msg);
        }
    };

    // TODO: rewrite https://stackoverflow.com/questions/17451660/one-click-event-handler-for-multiple-buttons
    submitButton.click(function(){
        let targetId = parseInt(targetIdField.val());
        let command = commandField.val();
        let commandPacket = { type: "command", command: command, computerID: targetId};

        responceField.val("");
        errorField.val("");

        ws.send(JSON.stringify(commandPacket));
    });



    // DEBUG STUFF

    let newTabButton = $("#new-tab");
    let tabNumber = 0;
    newTabButton.click(function(){
        tabNumber+=1;
        onNewConnection(tabNumber);
    });


    //why did I add ways to remove tabs, disconnects are probably only a sign that something is wrong?
    let removeTabButton = $("#remove-tab");
    removeTabButton.click(function(){
        if(tabNumber>0){
            tabNumber-=1;
            tabs.children("ul").children()[tabNumber].remove();
            tabs.children()[tabNumber+1].remove();
            tabs.tabs("refresh");
        }
    });

    let removeSpecificTabButton = $("#remove-specific-tab");
    let specificTabToRemove = $("#specific-tab-to-remove");
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


});

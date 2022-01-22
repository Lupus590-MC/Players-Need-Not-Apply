"use strict";
jQuery(document).ready(function(){
    "use strict";
    let $ = jQuery;

    let tabs = $("#tabs");
    tabs.tabs();

    /**
     * create a new tab for the new computer and set it up
     * @param  {number} ccComputerId
     * @param  {string} [ccComputerLabel]
     */
    function onNewConnection(ccComputerId, ccComputerLabel) {
        console.debug("Making tab for new connection with data: ", {ccComputerId, ccComputerLabel});

        let tabForComputerExists = $("#computer-"+ccComputerId)[0];
        if(tabForComputerExists){
            return;
        }

        if(ccComputerLabel === undefined || ccComputerLabel === null || ccComputerLabel === ""){
            ccComputerLabel = "";
        } else {
            ccComputerLabel = ccComputerLabel + " - ";
        }

        tabs.children("ul").append("<li><a href=\"#computer-"+ccComputerId+"\">"+ccComputerLabel+"#"+ccComputerId+"</a></li>");
        let clonedTemplate = $("#template").clone();
        clonedTemplate.attr("id", "computer-"+ccComputerId);
        clonedTemplate.appendTo(tabs);
        tabs.tabs("refresh");
    }

    let urlParams = new URLSearchParams(window.location.search);
    let WebSocketAddress = "ws://localhost:4000/"+(urlParams.get('ws') || "test");
    console.log("Connecting to "+WebSocketAddress);
    let ws = new WebSocket(WebSocketAddress);

    ws.onopen = function(evt){
        console.log("Connected");
        console.debug(evt);

        // ask any already connected CC computers to annouce themselves so that we know about them
        let soundOffPacket = {type: "soundOff"};
        ws.send(JSON.stringify(soundOffPacket));
    };




    //TODO: maybe use alert boxes for when the REPL errors?
    //TODO: rewrite to handle the tab system
    let targetIdField = $("#targetId");
    let commandField = $("#command");
    let submitButton = $("#submit");
    let responseField = $("#response");
    let errorField = $("#error");
    let statusFields = {};



    ws.onmessage = function (evt) {
        let received_msg = JSON.parse(evt.data);
        console.debug(received_msg);

        responseField.val("");
        errorField.val("");

        if(received_msg.type === "commandResponse"){
            let responseString = JSON.stringify(received_msg.response);
            responseField.val(responseString);
        } else if(received_msg.type === "error"){
            let errorInfoString = JSON.stringify(received_msg.errorInfo);
            errorField.val(errorInfoString);
        } else if(received_msg.type === "soundOffResponse"){
            let ccComputerId = received_msg.computerId;
            let ccComputerLabel = received_msg.computerLabel;
            onNewConnection(ccComputerId, ccComputerLabel);
        } else {
            console.warn("Unknown packet from pipe");
            console.warn(received_msg);
        }
    };

    // TODO: rewrite https://stackoverflow.com/questions/17451660/one-click-event-handler-for-multiple-buttons
    submitButton.click(function(){
        let targetId = parseInt(targetIdField.val());
        let command = commandField.val();
        let commandPacket = { type: "command", command: command, computerId: targetId};

        responseField.val("");
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

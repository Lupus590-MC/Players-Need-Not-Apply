jQuery(document).ready(function(){
    "use strict";
    let $ = jQuery;

    let tabs = $("#tabs");
    tabs.tabs();

    let waitingForConectionMessage = $("#waitingForConectionMessage");

    /**
     * create a new tab for the new computer and set it up
     * @param  {number} ccComputerId
     * @param  {string} [ccComputerLabel]
     */
    function onNewConnection(ccComputerId, ccComputerLabel) {
        console.debug("Making tab for new connection with data: ", {ccComputerId, ccComputerLabel});

        waitingForConectionMessage.hide();
        tabs.show();

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

    /**
     * puts the received string into the response field of the given computer's tab
     * @param {number} ccComputerId
     * @param {string} responseString
     */
    function setResponseField(ccComputerId, responseString){
        //TODO: can we be more intelligent here? maybe changing our data-section to ids?
        let computerTab = $("#computer-"+ccComputerId);
        computerTab.children().each(function(_index, value){
            value = $(value);
            if (value.data("section") === "command and respond"){
                let commandAndRespondSection = value;
                commandAndRespondSection.children("#response").val(responseString);
            }
        });
    }


    /**
     * proccesses reponse data from CC computers
     * @param  {number} ccComputerId
     * @param  {[boolean, ...]} responseData
     */
    function onCommandResponse(ccComputerId, responseData){
        console.debug({ccComputerId, responseData});
        let responseDataString = JSON.stringify(responseData);
        setResponseField(ccComputerId, responseDataString);
    }

    /**
     * processes errors from CC computer connection
     * errors in client code should be caught and returned as command responses so if this gets used then something is very wrong
     * @param  {number} ccComputerId
     * @param  {any} errorData
     * @param  {boolean} isFatal
     */
     function onError(ccComputerId, errorData, isFatal){
        //TODO: maybe use alert boxes for when the REPL errors?
        console.error({ccComputerId, errorData, isFatal});

        if(isFatal){
            // The CC computer has crashed if it doesn't connect again soon then it will need external assistance (a disk drive with a startup file)
        } else {
            // The CC computer messaging protocol has errored but recovered, any running tasks may have been interupted.
        }
    }

    /**
     * sends commands to the CC computer
     * @param {number} ccComputerId
     * @param {string} command
     */
    function onCommandButtonClick(ccComputerId, command){
        console.log("Sending command: "+command);
        let commandPacket = { type: "command", command: command, computerId: ccComputerId};
        setResponseField(ccComputerId, "");

        ws.send(JSON.stringify(commandPacket));
    }

    $(document).on("click", "#submit", function(e){
        let submitButton = $(e.target);
        let tabSection = submitButton.parent().parent();
        let commandField = submitButton.parent().children("#command");

        let commandString = commandField.val();
        let ccComputerId = tabSection.attr('id').replace("computer-", "");
        ccComputerId = parseInt(ccComputerId);
        onCommandButtonClick(ccComputerId, commandString);
    });

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

    ws.onmessage = function (evt) {
        let received_msg = JSON.parse(evt.data);
        console.debug(received_msg);

        if(received_msg.type === "commandResponse"){
            let response = received_msg.response;
            let ccComputerId = received_msg.computerId;
            onCommandResponse(ccComputerId, response);
        } else if(received_msg.type === "error"){
            let errorInfo = received_msg.errorInfo;
            let ccComputerId = received_msg.computerId;
            let isErrorFatal = received_msg.fatal;
            onError(ccComputerId, errorInfo, isErrorFatal);
        } else if(received_msg.type === "soundOffResponse"){
            let ccComputerId = received_msg.computerId;
            let ccComputerLabel = received_msg.computerLabel;
            onNewConnection(ccComputerId, ccComputerLabel);
        } else {
            console.warn("Unknown packet from pipe");
            console.warn(received_msg);
        }
    };
});

local ws = http.websocket("ws://localhost:4000/test")

while true do
    local commandAsJsonString = ws.receive()
    if commandAsJsonString then
        local commandData = textutils.unserialiseJSON(commandAsJsonString)
        if commandData and commandData.computerID == os.getComputerID() and commandData.type == "command" then
            local func, err = loadstring("return "..commandData.command)
            if func then
                local returns = table.pack(func())
                local returnsAsJsonString = textutils.serialiseJSON({ type = "commandResponce", responce = returns, computerID = os.getComputerID() })
                ws.send(returnsAsJsonString)
            else
                local errAsJsonString = textutils.serialiseJSON({ type = "error", errorMessage = err, computerID = os.getComputerID() })
                ws.send(errAsJsonString)
            end
        end
    end
end
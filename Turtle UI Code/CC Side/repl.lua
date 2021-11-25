local ws = http.websocket("ws://localhost:4000/test")

local function main()
    while true do
        local commandAsJsonString = ws.receive()
        if commandAsJsonString then
            local commandData = textutils.unserialiseJSON(commandAsJsonString)
            if commandData and commandData.computerID == os.getComputerID() and commandData.type == "command" then
                local func, err = loadstring("return "..commandData.command) -- BUG: missing shell API
                if func then
                    local returns = table.pack(pcall(func))
                    returns.n = nil
                    local returnsAsJsonString = textutils.serialiseJSON({ type = "commandResponce", responce = returns, computerID = os.getComputerID() })
                    ws.send(returnsAsJsonString)
                else
                    local errAsJsonString = textutils.serialiseJSON({ type = "error", errorMessage = err, computerID = os.getComputerID(), terminal = false })
                    ws.send(errAsJsonString)
                end
            end
        end
    end
end

local ok, err = pcall(main)

if not ok then
    local errAsJsonString = textutils.serialiseJSON({ type = "error", errorMessage = err, computerID = os.getComputerID(), terminal = true})
    ws.send(errAsJsonString)
    ws.close()
end
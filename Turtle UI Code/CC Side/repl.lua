local ws = http.websocket("ws://localhost:4000/test")
-- TODO: make this easier to read

local function main()
    while true do
        local commandAsJsonString = ws.receive()
        if commandAsJsonString then
            local commandData = textutils.unserialiseJSON(commandAsJsonString)
            if commandData and commandData.computerID == os.getComputerID() and commandData.type == "command" then
                local env = setmetatable({}, { __index = _ENV }) -- inherit our env, posibly not a good idea
                env._ENV = env
                local func, err = load("return "..commandData.command, nil,  nil, env)
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
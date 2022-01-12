local endpoint = arg[1] or "test"
local websocketAddress = "ws://localhost:4000/"..endpoint
local ws, err = http.websocket(websocketAddress)
if not ws then
	printError("Failed to connect to "..websocketAddress)
	error(err)
end
-- TODO: make this easier to read
-- TODO: announce self on the ws?
-- TODO: don't assume that ws is seccure, verify that message is from control center or toher trusted source - https://gist.github.com/MCJack123/7752c85918bcf23ada028abd615e8750
-- TODO: commands can be dropped if the computer is running a command already and happens to pull an event

local function main()
    while true do
        local commandAsJsonString = ws.receive()
        if commandAsJsonString then
            local commandData = textutils.unserialiseJSON(commandAsJsonString)
            if commandData and commandData.computerID == os.getComputerID() and commandData.type == "command" then
                local env = setmetatable({}, { __index = _ENV }) -- inherit our env, possibly not a good idea
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
    os.reboot() -- This is the bottom level program and in startup, rebooting when we error might help reconnect the computer to the control center
end

ws.close()

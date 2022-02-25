local endpoint = arg[2] or "00000000-0000-0000-0000-000000000000"
local websocketAddress = "ws://localhost:4000/"..endpoint
local ws, err = http.websocket(websocketAddress)
if not ws then
	printError("Failed to connect to "..websocketAddress)
	error(err, 0)
end

local mode = arg[1]
if not mode then
    error("mode (arg[1]) must be `snoop` or `command`", 0)
end
mode = mode:lower()

if mode == "snoop" then
    local pretty = require("cc.pretty")

    while true do
        local jsonString = ws.receive()
        local data = textutils.unserialiseJSON((jsonString))
        pretty.print(pretty.pretty(data))
    end
elseif mode == "command" then
    local commandHistory = {n=0}
    while true do
        local command = read(nil, commandHistory)
        commandHistory.n = commandHistory.n+1
        commandHistory[commandHistory.n] = command
        local data = textutils.serialiseJSON({type = "command", command = command, computerID = 3})
        ws.send(data)
    end
else
    error("mode (arg[1]) must be `snoop` or `command`", 0)
end

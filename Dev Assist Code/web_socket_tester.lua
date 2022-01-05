local ws = http.websocket("ws://localhost:4000/test")

local mode = arg[1]
if not mode then
    error("mode must be `snoop` or `command`", 0)
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
end
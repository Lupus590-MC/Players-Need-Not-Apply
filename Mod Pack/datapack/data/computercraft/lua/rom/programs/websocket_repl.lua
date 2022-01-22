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
	local errAsJsonString = textutils.serialiseJSON({ type = "soundOffResponse", computerId = os.getComputerID(), computerLabel = os.getComputerLabel() })
	ws.send(errAsJsonString)
	print("connected")

    while true do
        local commandAsJsonString = ws.receive()
        if commandAsJsonString then
            local commandData = textutils.unserialiseJSON(commandAsJsonString)
            if commandData and commandData.computerId == os.getComputerID() and commandData.type == "command" then
                local env = setmetatable({}, { __index = _ENV }) -- inherit our env, possibly not a good idea
                env._ENV = env
                local func, err = load("return "..commandData.command, nil,  nil, env)
                if func then
                    local returns = table.pack(pcall(func))
                    returns.n = nil
                    local returnsAsJsonString = textutils.serialiseJSON({ type = "commandResponce", responce = returns, computerId = os.getComputerID() })
                    ws.send(returnsAsJsonString)
                else
                    local errAsJsonString = textutils.serialiseJSON({ type = "error", errorInfo = err, computerId = os.getComputerID(), fatal = false })
                    ws.send(errAsJsonString)
                end
			elseif commandData and commandData.type == "soundOff" then
				local errAsJsonString = textutils.serialiseJSON({ type = "soundOffResponse", computerId = os.getComputerID(), computerLabel = os.getComputerLabel() })
                ws.send(errAsJsonString)
				print("sounding off")
            end
        end
    end
end

local ok, err = pcall(main)

if not ok then
    local errAsJsonString = textutils.serialiseJSON({ type = "error", errorInfo = err, computerId = os.getComputerID(), fatal = true})
    ws.send(errAsJsonString)
    ws.close()
    os.reboot() -- This is the bottom level program and in startup, rebooting when we error might help reconnect the computer to the control center
end

ws.close()

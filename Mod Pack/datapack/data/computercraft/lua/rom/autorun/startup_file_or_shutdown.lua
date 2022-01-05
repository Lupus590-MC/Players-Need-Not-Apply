-- delays first time boot so that the java app has time to set the websocket repl up

local function startupExists()
	if fs.exists("startup.lua") then
		return true
	end
	if fs.exists("startup") then
		if fs.isDir("startup") then
			if fs.list("startup")[1] then
				return true
			end
		else
			return true
		end
	end
	return false
end

for i = 1, 10 do
	if startupExists() then
		return
	end
	sleep(1)
end

if startupExists() then
	return
end
os.shutdown()

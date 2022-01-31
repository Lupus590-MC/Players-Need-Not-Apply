if not commands then
	error("requires command computer", 0)
end
local x, y, z = commands.getBlockPosition()

local info = commands.getBlockInfo(x, y+1, z)
require("cc.pretty").pretty_print(info)
local json = textutils.serialiseJSON(info)
local lua = textutils.serialise(info)

local f = fs.open("template.json", "w")
f.write(json)
f.close()

f = fs.open("template.lua", "w")
f.write("return "..lua)
f.close()

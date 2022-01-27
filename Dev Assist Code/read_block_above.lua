if not commands then
	error("requires command computer", 0)
end
local f = fs.open("template.txt", "w")
local x, y, z = commands.getBlockPosition()

local d = commands.getBlockInfo(x, y+1, z)
d = textutils.serialiseJSON(d)

f.write(d)
f.close()

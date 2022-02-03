if not commands then
	error("This program must run on a command computer.", 0)
end

local pretty = require("cc.pretty")
local expect = require("cc.expect")

-- TODO: find a nice spot to spawn
    -- Maybe remove saplings and other nice things after?

-- simple best effort converts "~" to absolute coords because getBlockInfo wants that
local function relativeCoordsToAbsolute(x, y, z)
    expect.expect(1, x, "string", "number")
    expect.expect(2, y, "string", "number")
    expect.expect(3, z, "string", "number")

    local commandX, commandY, commandZ = gps.locate()
    local function singleCoordConverter(maybeRelative, origin)
        if type(maybeRelative) == "string" and tonumber(maybeRelative) == nil then
            maybeRelative = maybeRelative:gsub("~", "")
            maybeRelative = tonumber(maybeRelative) or 0
            if maybeRelative then
                maybeRelative = origin + maybeRelative
            end
        end
        return maybeRelative
    end

    x = tonumber(x) or singleCoordConverter(x, commandX)
    y = tonumber(y) or singleCoordConverter(y, commandY)
    z = tonumber(z) or singleCoordConverter(z, commandZ)

    return x, y, z
end

local function getComputerID(x, y, z)
    expect.expect(1, x, "string", "number")
    expect.expect(2, y, "string", "number")
    expect.expect(3, z, "string", "number")

    local x, y, z = relativeCoordsToAbsolute(x, y, z)

    local computerData = commands.getBlockInfo(x, y, z)
    if computerData and computerData.nbt and computerData.nbt.ComputerId then
        return computerData.nbt.ComputerId
    else
        return nil, "invalid block"
    end
end


-- the players first turtle spawns in the overworld with:
    -- some fuel
    -- pickaxe
    -- chunk loader
    -- crafting table
-- while the turtle might be able to succed without the fuel (placing it at a tree in a forest would be good enough), we provide it as it's easier than trying to garantee that the turtle will spwn in an appropriate place
-- we also give them:
    -- chest (to make crafting easier)
    -- saplings (so that the turtle can set up a renewable source of fuel)
    -- dirt (to plant the saplings on)
    -- wireless modem (since location can be very important, this allows them to use GPS)
local function spawnOverworldTurtle(x, y, z)
    x = x or "~"
    y = y or "~1"
    z = z or "~"
    local returns = table.pack(commands.setBlock(x, y, z, "computercraft:turtle_advanced{Fuel: 1000, On: 1, LeftUpgrade:\"minecraft:diamond_pickaxe\", RightUpgrade: \"advancedperipherals:chunky_turtle\", Items: [{ id: \"minecraft:crafting_table\", Count: 1, Slot: 0 }, { id: \"minecraft:chest\", Count: 1, Slot: 1 },{ id: \"minecraft:birch_sapling\", Count: 10, Slot: 2}, { id: \"minecraft:spruce_sapling\", Count:10, Slot: 3 }, { id: \"minecraft:dirt\", Count:1, Slot: 4 }, { id: \"computercraft:wireless_modem\", Count:1, Slot: 5 }]}"))

    --pretty.pretty_print(returns)
    sleep(1)
    return getComputerID(x, y, z)
end

-- for spawning turtles in other dimentions (since going though a portal is difficult for a turtle)
-- not sure if I want to have these spawn with the original turtle and thus always be there for the player (and potentially be forgotten about) or if the player should have to give items to the command commputer (obsidian and eyes of ender for the nether and end respectivly) and then they spawn in that dimention
-- either way, we provide the absolute minimum to this turtle:
    -- item digitizer (so that the overworld turtle can send items to this turtle)
local function spawnOtherTurtle(x, y, z)
    x = x or "~"
    y = y or "~1"
    z = z or "~"
    local returns = table.pack(commands.setBlock(x, y, z, "computercraft:turtle_normal{Fuel: 0, On: 1, RightUpgrade: \"advancedperipherals:chunky_turtle\", Items: [{ id: \"digitalitems:item_digitizer\", Count: 1, Slot: 0 }]}"))

    --pretty.pretty_print(returns)
    sleep(1)
    return getComputerID(x, y, z)
end

local function listenOverworld()
    while true do
        fs.delete("request")
        fs.delete("response")
        fs.delete("ack")
        repeat
            sleep() -- TODO: better wait, this is probably going to be sleeping here most of the time, perhaps use a websocket to wake it up?
        until fs.exists("request")
        local turtleID = spawnOverworldTurtle()
        io.open("response", "w"):write(turtleID):close()
        repeat
            sleep()
        until fs.exists("ack")
    end
end

local function listenOther(world)
	local requiredResources = {
		-- world = {[itemId = count]}
		nether = {
			["minecraft:obsidian"] = 8,
			["minecraft:flint_and_steel"] = 1 -- TODO: damage these?
		},
		ender = {["minecraft:ender_eye"] = 12}
	}

	assert(requiredResources[world], "invalid world")
	local thisWorldResources = requiredResources[world]

	while true do
		fs.delete("request")
		fs.delete("response")
		fs.delete("ack")
		repeat
			sleep() -- TODO: better wait, this is probably going to be sleeping here most of the time, perhaps use a websocket to wake it up?
		until fs.exists("request")
		local requestFile = fs.open("request", "r")
		local requestData = requestFile.readAll()
		requestFile.close()
		local ok, offerings = pcall(function()
			local request = textutils.unserialise(requestData)

			-- convert shorthand to full names
			request.dim = request.dim == "o" and "overworld" or request.dim
			request.dim = request.dim == "n" and "nether" or request.dim
			request.dim = request.dim == "e" and "end" or request.dim

			return commands.getBlockInfo(request.x, request.y, request.z, request.dim)
		end);
		if ok then
			-- TODO: Can't set block across dim, may have to ask the local command computer to do it
			-- Could just not consume the items but still require them

			-- TODO: this allows empty inventories, it shouldn't
			local resourcesValid = true
			if offerings and offerings.nbt and offerings.nbt.items then
				local countedItems = {}
				for _, item in pairs(offerings.nbt.items) do
					if thisWorldResources[item.id] then
						if not countedItems[item.id] then
							countedItems[item.id] = 0
						end
						countedItems[item.id] = countedItems[item.id] + item.Count
					end
				end

				if not next(countedItems) then
					resourcesValid = false
					break
				end

				for item, count in pairs(countedItems) do
					if thisWorldResources[item] > count then
						resourcesValid = false
						break
					end
				end
			end

			if resourcesValid then
				local turtleID = spawnOtherTurtle() -- If we delete the items in the chest then we could accept/require a turtle and copy its NBT
				io.open("response", "w"):write(turtleID):close()
				repeat
					sleep()
				until fs.exists("ack")
			else
				io.open("response", "w"):write("Chest not located or doesn't contain the right items."):close() -- TODO: better message
			end
		else
			io.open("response", "w"):write("error reading request file"):close()
		end
	end
end

local function printUsage()
    print(arg[0].." test overworld")
    print(arg[0].." test other")
    print(arg[0].." listen overworld")
    print(arg[0].." listen nether")
    print(arg[0].." listen ender")
end

local mode = arg[1]
local world = arg[2]

if type(mode) ~= "string" or type(world) ~= "string" then
	printUsage()
	return
end

mode = mode:lower()
world = world:lower()

if mode == "test" then
	if world ~= "overworld" and world ~= "other" then
		printUsage()
		return
	end

	if world == "overworld" then
		print("turtleID: "..spawnOverworldTurtle())
	elseif world == "other" then
		print("turtleID: "..spawnOtherTurtle())
	end
elseif mode == "listen" then
	if world ~= "overworld" and world ~= "nether" and world ~= "ender" then
		printUsage()
		return
	end

	if world == "overworld" then
		listenOverworld()
	elseif world == "nether" or world == "ender" then
		listenOther(world)
	end
else
	printUsage()
end

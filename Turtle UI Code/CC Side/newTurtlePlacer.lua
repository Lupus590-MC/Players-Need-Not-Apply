local pretty = require("cc.pretty")

-- TODO: find a nice spot to spawn
    -- Maybe remove saplings and other nice things after?

local function spawnOverworldTurtle(x, y, z)
    x = x or "~"
    y = y or "~1"
    z = z or "~"
    local returns = table.pack(commands.setBlock(x, y, z, "computercraft:turtle_advanced{Fuel: 1000, On: 1, LeftUpgrade:\"minecraft:diamond_pickaxe\", RightUpgrade: \"advancedperipherals:chunky_turtle\", Items: [{ id: \"minecraft:crafting_table\", Count: 1, Slot: 0 }, { id: \"minecraft:chest\", Count: 1, Slot: 1 },{ id: \"minecraft:birch_sapling\", Count: 10, Slot: 2}, { id: \"minecraft:spruce_sapling\", Count:10, Slot: 3 }, { id: \"minecraft:dirt\", Count:1, Slot: 4 }. { id: \"advancedperipherals:geo_scanner\", Count:1, Slot: 5 }]}"))

    --pretty.pretty_print(returns)
end

local function spawnOtherTurtle(x, y, z)
    x = x or "~"
    y = y or "~1"
    z = z or "~"
    local returns = table.pack(commands.setBlock(x, y, z, "computercraft:turtle_normal{Fuel: 0, On: 1, Items: [{ id: \"digitalitems:item_digitizer\", Count: 1, Slot: 0 }]}"))

    --pretty.pretty_print(returns)
end

if arg[1] == "overworld" then
    spawnOverworldTurtle()
elseif arg[1] == "other" then
    spawnOtherTurtle()
else
    print(arg[0].." overworld")
    print(arg[0].." other")
end
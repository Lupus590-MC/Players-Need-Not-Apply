# data packets

## computer craft side

### error

```json
{
    "type": "error",
    "errorMessage": `string`,
    "computerID": `number`,
    "terminal": `bool`
}
```

Recived packet was not understood or running program has encountered an error.  If `terminal` is true then it means that the program is in the process of closing, otherwise it is probably ready for more commands.

### command responce

```json
{
    "type": "commandResponce",
    "responce": [bool, `any`, ...],
    "computerID": `number`
}
```

The first value of `responce` is from pcall, we can use this to know if the command succeded. A `false` here probably doesn't need to be the same level of caution as an `error` packet.

### status update

```json
{
    "type": "statusUpdate",
    "status": {
        "fuelLevel": `number`,
        "inventory": [`object of item data`, ...],
        "equippedItems": {"left": `object of item data`, "right": ...},
        "immediateSurroundings": {"up": `object of block info`, "down": ..., "front":...},
        "position": {"x": `number`, "y": `number`, "z": `number`, "dim": `string`
    },
    "computerID": `number`
}
```

Everything within the `status` object is optional, nil values should be assumed to have not changed. `status.position.dim` should be the name of the dimention that the computer is in, we will modify GPS to provide this.

<!--TODO: Add events and a file tree?-->

## control center side

### command

```json
{
    "type": "command",
    "command": `string``,
    "computerID": `number`
}
```

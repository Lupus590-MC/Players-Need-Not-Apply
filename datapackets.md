# data packets

Docs on the message packets for the REPL, likely will also be used for other things too.

## computer craft side

Packets sent from computercraft.

### error

```json
{
    "type": "error",
    "errorInfo": `string`,
    "computerId": `number`,
    "fatal": `bool`
}
```

Recived packet was not understood or REPL has encountered an error.  If `fatal` is true then it means that the program is in the process of closing, otherwise it thinks it can recover from the error and might be ready for more commands.

### command responce

```json
{
    "type": "commandResponce",
    "responce": [`bool`, `any`, ...],
    "computerId": `number`
}
```

The first value of `responce` is from pcall, we can use this to know if the command succeded. A `false` here doesn't need the same level of caution as an `error` packet as the REPL wasn't effected by the error.

### status update

```json
{
    "type": "statusUpdate",
    "status": {
        "fuelLevel": `number`,
        "inventory": [`object of item data`, ...],
        "equippedItems": {"left": `object of item data`, "right": ...},
        "immediateSurroundings": {"up": `object of block info`, "down": ..., "front":...},
        "position": {"x": `number`, "y": `number`, "z": `number`, "dim": `string`}
    },
    "computerId": `number`
}
```

Everything within the `status` object is optional, nil values should be assumed to have not changed. `status.position.dim` should be the name of the dimention that the computer is in, we will modify GPS to provide this.

### sound off resonse

```json
{
    "type": "soundOffResponce",
    "computerId": `number`,
    "computerLabel": `string`
}

Also sent by the CC computer when it boots up. We could send this multiple times, either because we rebooted/reconnected.

<!--TODO: Add events and a file tree?-->

## control center side

Packets sent from the web client.

### command

```json
{
    "type": "command",
    "command": `string`,
    "computerID": `number`
}
```

### sound off

```json
{
    "type": "soundOff"
}
```

Asks all computers to reply so that they can be added to the command interface, sent when the commmand interface connects to the web socket server.

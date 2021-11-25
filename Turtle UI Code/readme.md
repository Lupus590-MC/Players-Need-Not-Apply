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

## control center side
### command
```json
{
    "type": "command",
    "command": `string``,
    "computerID": `number`
}
```

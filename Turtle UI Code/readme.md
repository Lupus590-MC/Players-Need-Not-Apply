# data packets
## error
```json
{
    "type": "error",
    "errorMessage": `string`,
    "computerID": `number`
}
```
## command responce
```json
{
    "type": "commandResponce",
    "responce": [`any`, `any`, ...],
    "computerID": `number`
}
```
## command
```json
{
    "type": "command",
    "command": `string``,
    "computerID": `number`
}
```
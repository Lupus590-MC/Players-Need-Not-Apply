# data packets
## error
```json
{
    "type": "error",
    "errorMessage": `string`,
    "computerID": `number`
}
```
## comand responce
```json
{
    "type": "commandResponce",
    "responce": [`any`, `any`, ...],
    "computerID": `number`
}
```
## comand
```json
{
    "type": "command",
    "command": `string``,
    "computerID": `number`
}
```
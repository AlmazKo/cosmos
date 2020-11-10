# Binary structure

- 4 – tick's number
- 2 – number of messages
  - 1 – message's opcode
  - 1 – data length
  - N - data


Message's bytes: `4 + 2 + x*(2 + n) = 8..M`


# Json Structure
```json
{
  "tick": 1,
  "time": 1605009323,
  "messages": [
    {
      "id": 1,
      "action": "creature_moved",
      "type": "",
      "data": {
        "id": 1,
        "creatureId": 10069,
        "x": 5,
        "y": -4,
        "speed": 40,
        "mv": "NORTH",
        "sight": "NORTH"
      }
    }, {
      "id": 1,
      "action": "creature_moved",
      "type": "",
      "data": {
        "id": 1,
        "creatureId": 10068,
        "x": 14,
        "y": 4,
        "speed": 40,
        "mv": "NORTH",
        "sight": "NORTH"
      }
    }
  ]
}
```

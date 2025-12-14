# Guess The Number Game - cURL Commands

## Server Base URL
```
http://localhost:8080
```

---

## 🎮 MULTIPLAYER GAME FLOW

### Step 1: Player 1 - Start Multiplayer Game

```bash
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player1",
    "level": 1
  }'
```

**Response Example:**
```json
{
  "gameId": "abc123",
  "roomId": "room-xyz-789",
  "status": "IN_PROGRESS",
  "level": 1,
  "gameMode": "MULTIPLAYER",
  "playerId": "player1"
}
```

**⚠️ Save the `roomId` from this response for Player 2!**

---

### Step 2: Player 2 - Join Game (Using roomId from Player 1)

```bash
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player2",
    "level": 1,
    "roomId": "room-xyz-789"
  }'
```

**Replace `room-xyz-789` with the actual `roomId` from Player 1's response.**

**Response Example:**
```json
{
  "gameId": "def456",
  "roomId": "room-xyz-789",
  "status": "IN_PROGRESS",
  "level": 1,
  "gameMode": "MULTIPLAYER",
  "playerId": "player2"
}
```

**⚠️ Save the `gameId` from this response for making guesses!**

---

### Alternative: Player 2 - Auto-join (No roomId needed)

If Player 1 already started a game, Player 2 can auto-join:

```bash
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player2",
    "level": 1
  }'
```

This will automatically match Player 2 with Player 1's waiting room.

---

## 🎯 SUBMIT GUESSES

### Player 1 - Submit Guess

```bash
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "abc123",
    "playerId": "player1",
    "guess": "1234"
  }'
```

**Replace:**
- `abc123` with Player 1's actual `gameId`
- `1234` with your actual guess (4-digit number for level 1)

**Response Example:**
```json
{
  "correctDigits": 2,
  "guessNumber": 1,
  "status": "IN_PROGRESS",
  "guessedNumber": "1234",
  "remainingAttempts": 9
}
```

---

### Player 2 - Submit Guess

```bash
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "def456",
    "playerId": "player2",
    "guess": "5678"
  }'
```

**Replace:**
- `def456` with Player 2's actual `gameId`
- `5678` with your actual guess

---

## 🎲 SINGLE PLAYER GAME

### Start Single Player Game

```bash
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "SINGLE_PLAYER",
    "playerId": "player1",
    "level": 1
  }'
```

### Submit Guess (Single Player)

```bash
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "your-game-id",
    "playerId": "player1",
    "guess": "1234"
  }'
```

---

## 📜 GET GUESS HISTORY

```bash
curl -X GET http://localhost:8080/api/guess/history \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "your-game-id"
  }'
```

---

## 🛑 END GAME

### End Game (Multiplayer - Player 1 ends, Player 2 wins)

```bash
curl -X POST http://localhost:8080/api/guess/end \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "abc123",
    "playerId": "player1"
  }'
```

**Replace:**
- `abc123` with Player 1's actual `gameId`
- `player1` with the actual player ID

**Response Example (Multiplayer):**
```json
{
  "gameId": "abc123",
  "status": "COMPLETED",
  "message": "Game ended. Player player2 wins!",
  "hasWon": false,
  "winnerPlayerId": "player2"
}
```

**What happens:**
- Player 1's game is marked as `COMPLETED` with `hasWon: false`
- Player 2's game is marked as `COMPLETED` with `hasWon: true` (wins automatically)
- Game room status is set to `COMPLETED`
- Both games get an `endTime` timestamp

---

### End Game (Single Player)

```bash
curl -X POST http://localhost:8080/api/guess/end \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "your-game-id",
    "playerId": "player1"
  }'
```

**Response Example (Single Player):**
```json
{
  "gameId": "your-game-id",
  "status": "COMPLETED",
  "message": "Game ended. You did not complete the game.",
  "hasWon": false,
  "winnerPlayerId": null
}
```

**What happens:**
- Game is marked as `COMPLETED` with `hasWon: false`
- Game gets an `endTime` timestamp

---

## 📝 Complete Example Flow

### Terminal 1 (Player 1):
```bash
# 1. Start game
PLAYER1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{"gameMode": "MULTIPLAYER", "playerId": "player1", "level": 1}')

# 2. Extract roomId (requires jq: brew install jq)
ROOM_ID=$(echo $PLAYER1_RESPONSE | jq -r '.roomId')
GAME_ID_1=$(echo $PLAYER1_RESPONSE | jq -r '.gameId')

echo "Room ID: $ROOM_ID"
echo "Game ID: $GAME_ID_1"

# 3. Make a guess
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d "{\"gameId\": \"$GAME_ID_1\", \"playerId\": \"player1\", \"guess\": \"1234\"}"

# 4. End game (optional - Player 1 ends, Player 2 wins)
curl -X POST http://localhost:8080/api/guess/end \
  -H "Content-Type: application/json" \
  -d "{\"gameId\": \"$GAME_ID_1\", \"playerId\": \"player1\"}"
```

### Terminal 2 (Player 2):
```bash
# 1. Join game (replace ROOM_ID with value from Player 1)
PLAYER2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{"gameMode": "MULTIPLAYER", "playerId": "player2", "level": 1, "roomId": "ROOM_ID"}')

# 2. Extract gameId
GAME_ID_2=$(echo $PLAYER2_RESPONSE | jq -r '.gameId')

# 3. Make a guess
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d "{\"gameId\": \"$GAME_ID_2\", \"playerId\": \"player2\", \"guess\": \"5678\"}"
```

---

## 🔧 Notes

- **Level 1**: 4-digit number (default)
- **Level 2**: 5-digit number
- **Level 3**: 6-digit number
- Each player gets their own `gameId` in multiplayer mode
- Both players share the same `roomId` in multiplayer mode
- The `secretNumber` is auto-generated for single player, or can be set in multiplayer mode


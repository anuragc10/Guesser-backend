#!/bin/bash

# Guess The Number Game - cURL Commands
# Server runs on: http://localhost:8080

echo "========================================="
echo "Guess The Number Game - cURL Commands"
echo "========================================="
echo ""

# ============================================
# MULTIPLAYER GAME - Player 1 Starts Game
# ============================================
echo "1. Player 1 - Start Multiplayer Game:"
echo "--------------------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player1",
    "level": 1
  }'
EOF
echo ""
echo "Response will contain: gameId, roomId (save this for Player 2)"
echo ""

# ============================================
# MULTIPLAYER GAME - Player 2 Joins Game (Option 1: Using roomId from Player 1)
# ============================================
echo "2. Player 2 - Join Game Using roomId (from Player 1's response):"
echo "-----------------------------------------------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player2",
    "level": 1,
    "roomId": "YOUR_ROOM_ID_FROM_PLAYER1_RESPONSE"
  }'
EOF
echo ""
echo "Replace YOUR_ROOM_ID_FROM_PLAYER1_RESPONSE with the actual roomId from Player 1's response"
echo ""

# ============================================
# MULTIPLAYER GAME - Player 2 Joins Game (Option 2: Auto-match)
# ============================================
echo "3. Player 2 - Auto-join Waiting Room (if Player 1 already started):"
echo "--------------------------------------------------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "MULTIPLAYER",
    "playerId": "player2",
    "level": 1
  }'
EOF
echo ""
echo "This will automatically match with Player 1's waiting room"
echo ""

# ============================================
# SINGLE PLAYER GAME
# ============================================
echo "4. Start Single Player Game:"
echo "----------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/start \
  -H "Content-Type: application/json" \
  -d '{
    "gameMode": "SINGLE_PLAYER",
    "playerId": "player1",
    "level": 1
  }'
EOF
echo ""

# ============================================
# SUBMIT GUESS - Player 1
# ============================================
echo "5. Player 1 - Submit Guess:"
echo "----------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "YOUR_GAME_ID",
    "playerId": "player1",
    "guess": "1234"
  }'
EOF
echo ""
echo "Replace YOUR_GAME_ID with the actual gameId from start response"
echo "Replace '1234' with your actual guess (4-digit number for level 1)"
echo ""

# ============================================
# SUBMIT GUESS - Player 2
# ============================================
echo "6. Player 2 - Submit Guess:"
echo "----------------------------"
cat << 'EOF'
curl -X POST http://localhost:8080/api/guess/guess \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "YOUR_GAME_ID",
    "playerId": "player2",
    "guess": "5678"
  }'
EOF
echo ""
echo "Replace YOUR_GAME_ID with Player 2's gameId from start response"
echo "Replace '5678' with your actual guess"
echo ""

# ============================================
# GET GUESS HISTORY
# ============================================
echo "7. Get Guess History:"
echo "---------------------"
cat << 'EOF'
curl -X GET http://localhost:8080/api/guess/history \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": "YOUR_GAME_ID"
  }'
EOF
echo ""

echo "========================================="
echo "Example Complete Flow:"
echo "========================================="
echo ""
echo "# Step 1: Player 1 starts game"
echo "PLAYER1_RESPONSE=\$(curl -s -X POST http://localhost:8080/api/guess/start \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"gameMode\": \"MULTIPLAYER\", \"playerId\": \"player1\", \"level\": 1}')"
echo ""
echo "# Extract roomId from response (requires jq or manual extraction)"
echo "# ROOM_ID=\$(echo \$PLAYER1_RESPONSE | jq -r '.roomId')"
echo ""
echo "# Step 2: Player 2 joins using roomId"
echo "curl -X POST http://localhost:8080/api/guess/start \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d \"{\\\"gameMode\\\": \\\"MULTIPLAYER\\\", \\\"playerId\\\": \\\"player2\\\", \\\"level\\\": 1, \\\"roomId\\\": \\\"\$ROOM_ID\\\"}\""
echo ""
echo "# Step 3: Player 1 makes a guess"
echo "curl -X POST http://localhost:8080/api/guess/guess \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"gameId\": \"GAME_ID_1\", \"playerId\": \"player1\", \"guess\": \"1234\"}'"
echo ""
echo "# Step 4: Player 2 makes a guess"
echo "curl -X POST http://localhost:8080/api/guess/guess \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"gameId\": \"GAME_ID_2\", \"playerId\": \"player2\", \"guess\": \"5678\"}'"
echo ""


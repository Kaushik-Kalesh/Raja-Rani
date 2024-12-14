package io.kaushikkalesh.rajarani

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import android.util.Log
import io.socket.client.Ack

class SocketManager {
    private var socket: Socket? = null

    fun connect() {
        socket = IO.socket("http://192.168.29.222:3000")
        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("SocketManager", "Connected to server")
        }

        socket?.on("playerJoined") { args ->
            val data = args[0] as JSONObject
            val playerName = data.getString("playerName")
            Log.d("SocketManager", "Player joined: $playerName")
        }

        socket?.on("gameStarted") { args ->
            val data = args[0] as JSONObject
            val gameState = data.getJSONObject("gameState")
            Log.d("SocketManager", "Game started with state: $gameState")
        }
    }

    fun emitCreateRoom(playerName: String, callback: (String) -> Unit) {
        socket?.emit("createRoom", playerName, Ack { args ->
            val response = args[0] as JSONObject
            val roomCode = response.optString("roomCode", null.toString())
            callback(roomCode)
        })
    }

    fun emitJoinRoom(roomCode: String, playerName: String, callback: (Boolean, String?) -> Unit) {
        val data = JSONObject()
        data.put("roomCode", roomCode)
        data.put("playerName", playerName)

        socket?.emit("joinRoom", data, Ack { args ->
            val response = args[0] as JSONObject
            val success = response.optBoolean("success")
            val message = response.optString("error", null.toString())
            callback(success, message)
        })
    }

    fun disconnect() {
        Log.d("SocketManager", "Socket: ${socket?.id()} is connected?: ${socket?.connected()}")
        socket?.disconnect()
        Log.d("SocketManager", "Disconnected from server")
    }
}

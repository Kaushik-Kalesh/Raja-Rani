package io.kaushikkalesh.rajarani

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketManager {
    private var socket: Socket? = null

    fun connect() {
        socket = IO.socket("http://172.23.131.102:3000/")
        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("Socket", "Connected to server")
        }

        socket?.on("playerJoined") { args ->
            val data = args[0] as JSONObject
            val playerName = data.getString("playerName")
            Log.d("Socket", "Player joined: $playerName")
        }
    }

    fun emitPlayerLeft(roomCode: String) {
        socket?.emit("playerLeft", roomCode)
    }

    fun joinRoom(roomCode: String) {
        socket?.emit("joinRoom", roomCode)
    }

    fun disconnect() {
        socket?.disconnect()
    }
}

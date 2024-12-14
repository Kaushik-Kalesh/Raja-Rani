package io.kaushikkalesh.rajarani

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val socketManager = remember { SocketManager() }
    val coroutineScope = rememberCoroutineScope() // CoroutineScope tied to the composable lifecycle

    // Connect the socket once
    LaunchedEffect(Unit) {
        socketManager.connect()
    }

    NavHost(navController = navController, startDestination = "home/launch") {
        // Home screen UI
        composable("home/{flag}") { backStackEntry ->
            val flag = backStackEntry.arguments?.getString("flag") ?: ""
            HomePage(
                onCreateRoom = { playerName ->
                    socketManager.emitCreateRoom(playerName) { roomCode ->
                        coroutineScope.launch {
                            navController.navigate("room/$roomCode/$playerName")
                        }
                    }
                },
                onJoinRoom = { roomCode, playerName ->
                    socketManager.emitJoinRoom(roomCode, playerName) { success, message ->
                        coroutineScope.launch {
                            if (success) {
                                navController.navigate("room/$roomCode/$playerName")
                            } else {
                                Log.e("Socket Error", message ?: "Unknown error")
                                navController.navigate("home/invalid-room") {
                                    popUpTo("home/launch") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                },
                isRoomValid = flag != "invalid-room"
            )
        }

        // Room screen UI
        composable("room/{roomCode}/{playerName}") { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            RoomPage(roomCode = roomCode, playerName = playerName, socketManager = socketManager)
        }
    }

    // Disconnect the socket when the composable is disposed
    DisposableEffect(context) {
        onDispose {
            socketManager.disconnect()
        }
    }
}

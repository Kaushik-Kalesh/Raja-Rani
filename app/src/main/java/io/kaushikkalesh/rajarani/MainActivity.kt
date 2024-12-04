package io.kaushikkalesh.rajarani

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.kaushikkalesh.rajarani.ui.theme.RajaRaniTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RajaRaniTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "home/launch") {
        // Home screen UI
        composable("home/{flag}") { backStackEntry ->
            val flag = backStackEntry.arguments?.getString("flag") ?: ""
            HomePage(
                onCreateRoom = {
                    val call = RetrofitClient.apiService.createRoom()
                    call.enqueue(object : Callback<Room> {
                        override fun onResponse(call: Call<Room>, response: Response<Room>) {
                            if (response.isSuccessful) {
                                val roomCode = response.body()?.roomCode
                                navController.navigate("room/$roomCode")
                            }
                        }

                        override fun onFailure(call: Call<Room>, t: Throwable) {
                            t.message?.let { Log.e("RR_Server", it, t) }
                        }
                    })
                },
                onJoinRoom = { roomCode ->
                    val call = RetrofitClient.apiService.joinRoom(
                        JoinRoomRequest(
                            roomCode,
                            "Kaushik"
                        )
                    )
                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                navController.navigate("room/$roomCode")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            t.message?.let { Log.e("RR_Server", it, t) }
                            navController.navigate("home/invalid-room")
                        }
                    })
                },
                isRoomValid = flag != "invalid-room"
            )
        }

        // Room screen UI
        composable("room/{roomCode}") { backStackEntry ->
            // Retrieve roomCode from arguments
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            RoomPage(roomCode = roomCode)
        }
    }
}

@Composable
fun HomePage(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    isRoomValid: Boolean
) {
    var roomCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Raja Rani", fontSize = 64.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onCreateRoom() },
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text("Create Room")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = roomCode,
                onValueChange = { roomCode = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Enter Room Code") }
            )

            if (!isRoomValid) {
                Text(
                    text = "Invalid Room Code",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onJoinRoom(roomCode) },
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text("Join Room")
            }
        }
    }
}

@Composable
fun RoomPage(roomCode: String) {
    val socketManager = remember { SocketManager() }

    LaunchedEffect(roomCode) {
        socketManager.connect()
        socketManager.joinRoom(roomCode)
    }

    DisposableEffect(roomCode) {
        onDispose {
            socketManager.emitPlayerLeft(roomCode)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Code: $roomCode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

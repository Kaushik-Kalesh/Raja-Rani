package io.kaushikkalesh.rajarani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.kaushikkalesh.rajarani.ui.theme.RajaRaniTheme
import kotlin.random.Random

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
    NavHost(navController = navController, startDestination = "home/launch") {
        // Home screen UI
        composable("home/{flag}") { backStackEntry ->
            val flag = backStackEntry.arguments?.getString("flag") ?: ""
            HomePage(
                onCreateRoom = {
                    val roomCode = Random.nextInt(1000, 10000)
                    navController.navigate("room/$roomCode")
                },
                onJoinRoom = { roomCode ->
                    if (roomCode in 1000..9999) {
                        navController.navigate("room/$roomCode")
                    } else {
                        // Handle invalid room code
                        navController.navigate("home/invalid-room")
                    }
                },
                isRoomValid = flag != "invalid-room"
            )
        }

        // Room screen UI
        composable("room/{roomCode}") { backStackEntry ->
            // Retrieve roomCode from arguments
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            RoomPage(roomCode = roomCode.toInt())
        }
    }
}

@Composable
fun HomePage(
    onCreateRoom: () -> Unit,
    onJoinRoom: (Int) -> Unit,
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
                onClick = { onJoinRoom(roomCode.toIntOrNull() ?: -1) },
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text("Join Room")
            }
        }
    }
}

@Composable
fun RoomPage(roomCode: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Code: $roomCode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

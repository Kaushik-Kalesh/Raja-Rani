package io.kaushikkalesh.rajarani

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePage(
    onCreateRoom: (String) -> Unit,
    onJoinRoom: (String, String) -> Unit,
    isRoomValid: Boolean
) {
    var roomCode by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }

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

            TextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onCreateRoom(playerName) },
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text("Create Room")
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = roomCode,
                onValueChange = { roomCode = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Enter Room Code") },
                modifier = Modifier.width(170.dp)
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
                onClick = { onJoinRoom(roomCode, playerName) },
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text("Join Room")
            }
        }
    }
}
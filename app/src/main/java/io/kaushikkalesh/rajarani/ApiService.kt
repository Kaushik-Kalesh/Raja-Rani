package io.kaushikkalesh.rajarani

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Room(val roomCode: String, val players: List<String>)

data class JoinRoomRequest(val roomCode: String, val playerName: String)

interface ApiService {
    @POST("/rooms/create")
    fun createRoom(): Call<Room>

    @POST("/rooms/join")
    fun joinRoom(@Body request: JoinRoomRequest): Call<Void>

    @GET("/rooms/{roomCode}")
    fun getRoomDetails(@Path("roomCode") roomCode: String): Call<Room>
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://172.23.131.102:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

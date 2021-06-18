package at.campus02.mob.quizapp

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

// Definition des API Zugriffs als Interface
interface RestApi {

    @POST("/quiz/{username}")
    fun startGameFor(@Path("username") username: String): Deferred<Response<Game>>

    @POST("/quiz/{username}/game/{gameId}/answer")
    fun answer(
        @Path("username") username: String,
        @Path("gameId") gameId: Int,
        @Body question: Question
    ): Deferred<Response<Game>>

}

// Konfiguration und Erstellen des API Zugriffs
val api = Retrofit.Builder()
    // adapter, damit wir keine Callbacks verwenden müssen, sondern mit "suspend/await" arbeiten können
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    // converter für JSON (wir verwenden Moshi) - baut Kotlin Klassen aus dem JSON vom Server
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder().build()
        )
    )
    // Wo liegt das API eigentlich?
    .baseUrl("http://quiz.moarsoft.com:8080")
    // Konfiguration bauen lassen
    .build()
    // Implementierung des RestApi Interface erzeugen lassen
    .create(RestApi::class.java)


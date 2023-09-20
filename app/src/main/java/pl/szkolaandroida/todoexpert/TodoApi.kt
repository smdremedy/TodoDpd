package pl.szkolaandroida.todoexpert

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TodoApi {

    /*
    curl -X POST \
  -H "X-Parse-Application-Id: ${APPLICATION_ID}" \
  -H "X-Parse-REST-API-Key: ${REST_API_KEY}" \
  -H "X-Parse-Revocable-Session: 1" \
  -H "Content-Type: application/json" \
  -d '{"username":"cooldude6","password":"p_n7!-e8"}' \
  https://YOUR.PARSE-SERVER.HERE/parse/login
     */

    @Headers(
        "X-Parse-REST-API-Key: ${REST_API_KEY}",
        "X-Parse-Revocable-Session: 1",
        "Content-Type: application/json"
    )
    @POST("login")
    fun login(
        @Body request: LoginRequest,
        @Header("X-Parse-Application-Id") applicationId: String
    ): Call<LoginResponse>


    @Headers(
        "X-Parse-REST-API-Key: ${REST_API_KEY}",
        "X-Parse-Application-Id: $APP_ID"
    )
    @GET("classes/Todo")
    fun getTodos(@Header("X-Parse-Session-Token") token: String): Call<TodosResponse>

    companion object {
        private const val REST_API_KEY = "LCTpX53aBmbtIXOtFmDb9dklESKUd0q58hFbnRYc"
        const val APP_ID = "X7HiVehVO7Zg9ufo0qCDXVPI3z0bFpUXtyq2ezYL"
    }
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("objectId") val id: String,
    val sessionToken: String
)

data class TodosResponse(
    val results: List<Todo>
)

data class Todo(
    val content: String,
    val done: Boolean
)
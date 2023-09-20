package pl.szkolaandroida.todoexpert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TodosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todos)


        val retrofit = Retrofit.Builder()
            .addConverterFactory((GsonConverterFactory.create()))
            .baseUrl("https://parseapi.back4app.com")
            .build()

        val api = retrofit.create(TodoApi::class.java)

        val call = api.getTodos(intent.getStringExtra("token") ?: "")
        call.enqueue(object : Callback<TodosResponse> {
            override fun onResponse(
                call: Call<TodosResponse>,
                response: Response<TodosResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        for (todo in it.results) {
                            Log.d("TAG", "Todo:$todo")
                        }
                    }
                } else {
                    Log.w("TAG", "unsuccessful")
                }
            }

            override fun onFailure(call: Call<TodosResponse>, t: Throwable) {
                Log.e("TAG", t.toString())
            }

        })

    }
}
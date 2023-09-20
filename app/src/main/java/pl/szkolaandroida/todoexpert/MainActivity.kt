package pl.szkolaandroida.todoexpert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.constraintlayout.motion.utils.ViewState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.szkolaandroida.todoexpert.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.contains("token")) {
            val intent = Intent(this, TodosActivity::class.java)
            intent.putExtra("token", sharedPreferences.getString("token", ""))
            startActivity(intent)
            finish()
            return
        }
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            //viewModel.login(username, password)
            viewModel.executeAction(LoginClickedAction(username, password))

        }

        //binding.login.onClickAction(LoginClickedAction(username, password))

        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    private fun render(viewState: LoginViewState) {
        binding.username.setError(viewState.usernameError)

        if (viewState.isCompleted) {
            val intent = Intent(this, TodosActivity::class.java)
            (this.application as App).token = viewState.token!!

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.edit()
                .putString("token", viewState.token!!)
                .apply()
            intent.putExtra("token", viewState.token)
            startActivity(intent)
        }
    }

    fun Button.onClickAction(action: Action) {
        this.setOnClickListener { viewModel.executeAction(action) }
    }
}


class LoginViewModel : ViewModel() {

    val viewState = MutableLiveData(LoginViewState())

    fun executeAction(action: Action) {
        when (action) {
            is LoginClickedAction -> login(action.username, action.password)
        }
    }

    fun login(username: String, password: String) {

        if (username.isEmpty()) {
            viewState.postValue(viewState.value!!.copy(usernameError = "Can't be empty!"))
            return
        }

        val request = LoginRequest(username, password = password)

        val retrofit = Retrofit.Builder()
            .addConverterFactory((GsonConverterFactory.create()))
            .baseUrl("https://parseapi.back4app.com")
            .build()

        val api = retrofit.create(TodoApi::class.java)

        val call = api.login(request, TodoApi.APP_ID)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("TAG", "token:${response.body()?.sessionToken}")
                    viewState.postValue(
                        viewState.value!!.copy(
                            isCompleted = true,
                            token = response.body()?.sessionToken,
                            usernameError = null
                        )
                    )
                } else {
                    Log.w("TAG", "unsuccessful")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("TAG", t.toString())
            }

        })

    }
}

data class LoginViewState(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isCompleted: Boolean = false,
    val token: String? = null
)

sealed interface Action

class LoginClickedAction(val username: String, val password: String) : Action
package pl.szkolaandroida.todoexpert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.szkolaandroida.todoexpert.databinding.ActivityTodosBinding
import pl.szkolaandroida.todoexpert.databinding.TodoItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TodosActivity : AppCompatActivity() {

    private val adapter = TodosAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTodosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.todosRv.layoutManager = GridLayoutManager(this, 2)
        binding.todosRv.adapter = adapter


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
                        adapter.setTodos(it.results)
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

class TodosAdapter : RecyclerView.Adapter<TodoViewHolder>() {

    private val todos = mutableListOf<Todo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodoViewHolder(TodoItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.bind(todo)
    }

    fun setTodos(results: List<Todo>) {
        todos.clear()
        todos.addAll(results)
        this.notifyDataSetChanged()
    }

}

class TodoViewHolder(val binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todo: Todo) {
        binding.done.isChecked = todo.done
        binding.content.text = todo.content
    }

}
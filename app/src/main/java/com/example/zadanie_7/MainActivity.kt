package com.example.zadanie_7

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity(), Adapter.OnItemClickListener {

    private var isFirstLaunch = false
    private lateinit var login: String
    private lateinit var recycler_view: RecyclerView
    private var exampleList = ArrayList<Comment>()
    private lateinit var adapter: Adapter
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfFirstLaunch()
        if (isFirstLaunch) {
            goToSettings()
        } else {
            loadLogin()
            Toast.makeText(applicationContext, "Welcome $login", Toast.LENGTH_SHORT).show()

            getCurrentData()

            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        while (!isInterrupted) {
                            sleep(60000)
                            runOnUiThread {
                                getCurrentData()
                            }
                        }
                    } catch (e: InterruptedException) {
                    }
                }
            }
            thread.start()

            swipeContainer = findViewById(R.id.swipeContainer)
            swipeContainer.setOnRefreshListener {
                getCurrentData()
                swipeContainer.setRefreshing(false);
            }

            adapter = Adapter(exampleList, this)

            val button: Button = findViewById(R.id.buttonSendComment)
            val comment_input: EditText = findViewById(R.id.commentInput)

            recycler_view = findViewById(R.id.recyclerView)
            recycler_view.adapter = Adapter(exampleList, this)
            recycler_view.layoutManager = LinearLayoutManager(this)
            recycler_view.setHasFixedSize(true)
            recycler_view.scrollToPosition(exampleList.size - 1)

            val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (getLogin() == viewHolder.itemView.findViewById<TextView>(R.id.login).text.toString()) {
                        val position = viewHolder.adapterPosition
                        exampleList.removeAt(position)
                        recycler_view.adapter?.notifyItemRemoved(position)
                        deleteComment(viewHolder.itemView.findViewById<TextView>(R.id.id).text.toString())
                    }
                    getCurrentData()
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(recycler_view)

            button.setOnClickListener {
                addComment()
                comment_input.setText("")
                comment_input.setHint("Content")
                getCurrentData()
            }
        }

        val nav_view:NavigationView = findViewById<NavigationView>(R.id.navView)
        nav_view.setNavigationItemSelectedListener { menuItem ->

            if (menuItem.itemId == R.id.nav_settings) {
                goToSettings()
            }

            true
        }
    }

    private fun goToSettings() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun checkIfFirstLaunch() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        isFirstLaunch = sharedPreferences.getBoolean("LAUNCH_KEY", true)
    }

    private fun loadLogin() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        login = sharedPreferences.getString("LOGIN_KEY", "Guest").toString()
    }

    private fun getCurrentData() {
        val connectionInternetErrorMainActivity: TextView = findViewById(R.id.connectionInternetErrorMainActivity)

        if (checkForInternet(this) == false) {
            connectionInternetErrorMainActivity.setVisibility(View.VISIBLE)
        } else {
            connectionInternetErrorMainActivity.setVisibility(View.INVISIBLE)

            val api = Retrofit.Builder()
                .baseUrl(JsonAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JsonAPI::class.java)

            api.getComments().enqueue(object : Callback<List<FetchedComment>> {
                override fun onFailure(call: Call<List<FetchedComment>>, t: Throwable) {
                    Log.e("MainActivity", "ERROR: $t")
                }

                override fun onResponse(call: Call<List<FetchedComment>>, response: Response<List<FetchedComment>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            for (FetchedComment in it) {
                                exampleList.add(
                                    Comment(
                                        FetchedComment.content,
                                        FetchedComment.login,
                                        FetchedComment.date.split("T")[0] + "  " + FetchedComment.date.split("T")[1].split(".")[0],
                                        FetchedComment.id
                                    )
                                )
                            }
                        }
                    }

                    val recycler_view = findViewById<RecyclerView>(R.id.recyclerView)
                    if(recycler_view != null) {
                        recycler_view.scrollToPosition(exampleList.size - 1)
                    }
                }
            })
        }
    }

    private fun getLogin(): String {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        login = sharedPreferences.getString("LOGIN_KEY", "Guest").toString()

        return login
    }

    private fun addComment() {
        val api = Retrofit.Builder()
            .baseUrl(JsonAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonAPI::class.java)

        api.addComment(AddComment(getLogin(), findViewById<EditText>(R.id.commentInput).text.toString())).enqueue(object : Callback<FetchedComment> {
            override fun onFailure(call: Call<FetchedComment>, t: Throwable) {
                Log.e("MainActivity", "ERROR: $t")
            }

            override fun onResponse(call: Call<FetchedComment>, response: Response<FetchedComment>) {
                if (response.isSuccessful) {

                }

                val recycler_view = findViewById<RecyclerView>(R.id.recyclerView)
                if(recycler_view != null) {
                    recycler_view.scrollToPosition(exampleList.size - 1)
                }
            }
        })
    }

    private fun deleteComment(id: String) {
        val api = Retrofit.Builder()
            .baseUrl(JsonAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonAPI::class.java)

        api.deleteComment(id).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditPostActivity", "ERROR: $t")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
        })
    }

    override fun onClick(id: String, login: String, content: String, date: String) {
        if (getLogin() == login) {
            val intent = Intent(this@MainActivity, EditPostActivity::class.java)
            val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("ID_KEY", id)
            editor.putString("EDIT_LOGIN_KEY", login)
            editor.putString("CONTENT_KEY", content)
            editor.putString("DATE_KEY", date)
            editor.apply()
            startActivity(intent)
        } else {
            Toast.makeText(this, "Wrong login!", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
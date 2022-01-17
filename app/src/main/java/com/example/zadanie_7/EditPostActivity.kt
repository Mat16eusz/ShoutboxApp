package com.example.zadanie_7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditPostActivity : AppCompatActivity() {

    private lateinit var id: String
    private lateinit var login: String
    private lateinit var content: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        id = sharedPreferences.getString("ID_KEY", "NULL").toString()
        login = sharedPreferences.getString("EDIT_LOGIN_KEY", "Guest").toString()
        content = sharedPreferences.getString("CONTENT_KEY", "Test").toString()
        date = sharedPreferences.getString("DATE_KEY", "0000-00-00 00:00:00").toString()

        val idText = findViewById<TextView>(R.id.editId)
        val loginText = findViewById<TextView>(R.id.editLogin)
        val contentText = findViewById<TextView>(R.id.editContent)
        val dateText = findViewById<TextView>(R.id.editDate)

        idText.setText(id)
        loginText.setText(login)
        contentText.setText(content)
        dateText.setText(date)

        val editButton: Button = findViewById(R.id.editComment)
        editButton.setOnClickListener {
            editComment(id, login, findViewById<EditText>(R.id.editContent).text.toString())
            goToShoutbox()
        }

        val deleteButton: Button = findViewById(R.id.deleteComment)
        deleteButton.setOnClickListener {
            deleteComment(id)
            goToShoutbox()
        }
    }

    private fun goToShoutbox() {
        val intent = Intent(this@EditPostActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun editComment(id: String, login: String, content: String) {
        val api = Retrofit.Builder()
            .baseUrl(JsonAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonAPI::class.java)

        api.editComment(id, login, content).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditPostActivity", "ERROR: $t")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

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
}
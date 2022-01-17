package com.example.zadanie_7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.navigation.NavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val confirmButton: Button = findViewById(R.id.setLoginButton)

        confirmButton.setOnClickListener {
            val login: String = loginInput.text.toString().trim()

            if (login.isNullOrBlank()){
                Toast.makeText(
                    applicationContext,
                    "Please enter username first!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                confirmFirstLaunch()
                saveLogin(login)
                goToShoutbox()
            }
        }

        val nav_view: NavigationView = findViewById<NavigationView>(R.id.navView)
        nav_view.setNavigationItemSelectedListener { menuItem ->

            if (menuItem.itemId == R.id.nav_shoutbox) {
                goToShoutbox()
            }

            true
        }
    }

    private fun goToShoutbox() {
        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun confirmFirstLaunch() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("LAUNCH_KEY", false)
        editor.apply()
    }

    private fun saveLogin(login: String) {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("LOGIN_KEY", login)
        editor.apply()
    }
}
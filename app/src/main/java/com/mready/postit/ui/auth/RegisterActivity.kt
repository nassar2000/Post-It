package com.mready.postit.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mready.postit.MainActivity
import com.mready.postit.R
import com.mready.postit.helper.SharedPrefManager


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val isLoggedIn: Boolean = SharedPrefManager(this).isLoggedIn()

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout_auth, LoginFragment.newInstance()).commit()
    }
}
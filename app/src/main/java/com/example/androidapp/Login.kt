package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import android.widget.EditText


class Login : AppCompatActivity() {
    private lateinit var btnLogin : Button
    private lateinit var tvRegister : TextView
    private lateinit var etLogin : EditText
    private lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.btnLogin = findViewById(R.id.login_button)
        this.tvRegister = findViewById(R.id.register_link)
        this.etLogin = findViewById(R.id.email)
        this.etPassword = findViewById(R.id.password)

        this.tvRegister.setOnClickListener{onClickRegister()}
    }

    fun onClickRegister(){
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}
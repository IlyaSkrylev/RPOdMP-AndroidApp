package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowId
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class Main : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var tvLogOut: TextView
    private lateinit var ivLogOut: ImageView
    private lateinit var tvProfile: TextView
    private lateinit var ivProfile: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        this.auth = Firebase.auth
        this.user = auth.currentUser
        if (user == null){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        this.tvLogOut = findViewById(R.id.logout_label)
        this.ivLogOut = findViewById(R.id.logout_icon)
        this.tvProfile = findViewById(R.id.profile_label)
        this.ivProfile = findViewById(R.id.profile_icon)

        this.tvLogOut.setOnClickListener{ logOut() }
        this.ivLogOut.setOnClickListener{ logOut() }
        this.tvProfile.setOnClickListener{ showProfilePage() }
        this.ivProfile.setOnClickListener{ showProfilePage() }
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProfilePage(){
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
        finish()
    }
}
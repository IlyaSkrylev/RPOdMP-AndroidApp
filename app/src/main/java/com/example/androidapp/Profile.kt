package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    private lateinit var tvLogOut: TextView
    private lateinit var ivLogOut: ImageView
    private lateinit var tvBack: TextView
    private lateinit var ivBack: ImageView
    private lateinit var btnSave: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.auth = Firebase.auth
        this.user = auth.currentUser
        if (user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        this.tvLogOut = findViewById(R.id.logout_label)
        this.ivLogOut = findViewById(R.id.logout_icon)
        this.tvBack = findViewById(R.id.back_label)
        this.ivBack = findViewById(R.id.back_icon)
        this.btnSave = findViewById(R.id.save_button)

        this.tvLogOut.setOnClickListener{ logOut() }
        this.ivLogOut.setOnClickListener{ logOut() }
        this.tvBack.setOnClickListener{ showMainPage() }
        this.ivBack.setOnClickListener{ showMainPage() }
        this.btnSave.setOnClickListener{ saveChanges() }
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun showMainPage() {
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveChanges(){

        database = FirebaseDatabase.getInstance().getReference("Users")
    }
}
package com.example.androidapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.databinding.ActivityProfileBinding
import com.example.androidapp.databinding.ActivitySmartphoneInfoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SmartphoneInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivitySmartphoneInfoBinding
    private lateinit var database: DatabaseReference
    private var uidSmartphone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.auth = Firebase.auth
        this.user = auth.currentUser
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        this.binding = ActivitySmartphoneInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        this.uidSmartphone = intent.getStringExtra("uidSmartphone")
        if (uidSmartphone.toString().isEmpty()){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            showMainPage()
        }

        this.binding.backIcon.setOnClickListener { showMainPage() }
        this.binding.backLabel.setOnClickListener { showMainPage() }

        showSmartphoneInfo()
    }

    private fun showMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showSmartphoneInfo(){
        database = FirebaseDatabase.getInstance().getReference("smartphones").child(uidSmartphone.toString())
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val name = childSnapshot.child("name").getValue(String::class.java)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
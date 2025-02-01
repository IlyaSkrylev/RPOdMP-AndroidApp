package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidapp.databinding.ActivityMainBinding
import com.example.androidapp.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        this.auth = Firebase.auth
        this.user = auth.currentUser
        if (user == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        this.binding.logoutLabel.setOnClickListener{ logOut() }
        this.binding.logoutIcon.setOnClickListener{ logOut() }
        this.binding.profileLabel.setOnClickListener{ showProfilePage() }
        this.binding.profileIcon.setOnClickListener{ showProfilePage() }

        ShowSmartphones()
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProfilePage(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun ShowSmartphones() {
        db = FirebaseFirestore.getInstance()
        db.collection("smartphones")
            .get()
            .addOnSuccessListener { smartphones ->
                for (smarphone in smartphones) {
                    try {
                        val smartphoneInfo = smarphone.toObject(Smartphone::class.java)

                        val inflater = LayoutInflater.from(this)
                        val view = inflater.inflate(R.layout.smatrphone_item, this.binding.smartphones, false)

                        val phoneImage = view.findViewById<ImageView>(R.id.phone_image)
                        Glide.with(this).load(smartphoneInfo.base64Images[0]).into(phoneImage)

                        view.findViewById<TextView>(R.id.phone_name).setText(smartphoneInfo.name)
                        view.findViewById<TextView>(R.id.phone_color).setText(getString(R.string.color) + "s: " + smartphoneInfo.color)
                        view.findViewById<TextView>(R.id.phone_memory).setText(getString(R.string.memory) + ": " + smartphoneInfo.builtInMemory
                                + "/" + smartphoneInfo.ramMemory)
                        view.findViewById<TextView>(R.id.phone_camera).setText(getString(R.string.camera) + ": " + smartphoneInfo.rearCameraResolution
                                + "/" + smartphoneInfo.frontCameraResolution)

                        val fav = view.findViewById<ImageView>(R.id.favorites)
                        fav.setOnClickListener{ addOrDeleteFromFavorites(fav) }

                        val item = view.findViewById<FrameLayout>(R.id.item)
                        item.setOnClickListener{ showSmartphoneInformationPage(smartphoneInfo.ean) }

                        this.binding.smartphones.addView(view)
                    }catch (e: Exception){
                    }
                }
            }.addOnFailureListener {
            }
    }

    fun addOrDeleteFromFavorites(imageView: ImageView){

    }

    fun  showSmartphoneInformationPage(ean: String?){

        val intent = Intent(this, SmartphoneInfoActivity::class.java)
        intent.putExtra("uidSmartphone", ean)
        startActivity(intent)
        finish()
    }
}
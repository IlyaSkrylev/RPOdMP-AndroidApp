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
import android.widget.Toast
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
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        this.binding.favoritesLabel.setOnClickListener { showFavoritesPage() }
        this.binding.favoritesIcon.setOnClickListener { showFavoritesPage() }
        this.binding.profileLabel.setOnClickListener { showProfilePage() }
        this.binding.profileIcon.setOnClickListener { showProfilePage() }

        ShowSmartphones()
    }

    fun showFavoritesPage() {
        val intent = Intent(this, FavoritesActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProfilePage() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId", "UseCompatLoadingForDrawables")
    fun ShowSmartphones() {
        val userId = user?.uid
        if (userId == "" || userId == null) return

        db = FirebaseFirestore.getInstance()
        db.collection("smartphones")
            .get()
            .addOnSuccessListener { smartphones ->
                for (smarphone in smartphones) {
                    try {
                        val smartphoneInfo = smarphone.toObject(Smartphone::class.java)

                        val inflater = LayoutInflater.from(this)
                        val view = inflater.inflate(
                            R.layout.smatrphone_item,
                            this.binding.smartphones,
                            false
                        )

                        val phoneImage = view.findViewById<ImageView>(R.id.phone_image)
                        Glide.with(this).load(smartphoneInfo.base64Images[0]).into(phoneImage)

                        view.findViewById<TextView>(R.id.phone_name).setText(smartphoneInfo.name)
                        view.findViewById<TextView>(R.id.phone_color)
                            .setText(getString(R.string.color) + "s: " + smartphoneInfo.color)
                        view.findViewById<TextView>(R.id.phone_memory).setText(
                            getString(R.string.memory) + ": " + smartphoneInfo.builtInMemory
                                    + "/" + smartphoneInfo.ramMemory
                        )
                        view.findViewById<TextView>(R.id.phone_camera).setText(
                            getString(R.string.camera) + ": " + smartphoneInfo.rearCameraResolution
                                    + "/" + smartphoneInfo.frontCameraResolution
                        )

                        val fav = view.findViewById<ImageView>(R.id.favorites)
                        fav.setOnClickListener { addOrDeleteFromFavorites(fav, smartphoneInfo.ean) }

                        db.collection("favorites").whereEqualTo("userId", userId).limit(1).get()
                            .addOnSuccessListener { favorite ->
                                if (!favorite.isEmpty) {
                                    val document = favorite.first()
                                    val favoriteInfo = document.toObject(Favorite::class.java)

                                    if (smartphoneInfo.ean in favoriteInfo.smartphonesId){
                                        fav.setImageDrawable(getDrawable(R.drawable.in_favorites))
                                    }
                                }
                            }


                        val item = view.findViewById<FrameLayout>(R.id.item)
                        item.setOnClickListener { showSmartphoneInformationPage(smartphoneInfo.ean) }

                        this.binding.smartphones.addView(view)
                    } catch (e: Exception) {
                    }
                }
            }.addOnFailureListener {
            }
    }

    fun addOrDeleteFromFavorites(imageView: ImageView, smartphoneId: String?) {
        val userId = user?.uid
        val userEmail = user?.email
        var fav = Favorite(
            "",
            "",
            emptyList()
        )
        var unicId = ""

        if (userId == null || userId == "" || userEmail == null || userEmail == "" || smartphoneId == null) return

        db = FirebaseFirestore.getInstance()
        db.collection("favorites").whereEqualTo("userId", userId).limit(1).get()
            .addOnSuccessListener { favorite ->
                if (!favorite.isEmpty) {
                    val document = favorite.first()
                    val favoriteInfo = document.toObject(Favorite::class.java)
                    fav = favoriteInfo
                    unicId = document.id
                }

                fav.userEmail = userEmail
                fav.userId = userId

                if (imageView.drawable?.constantState?.hashCode() ==
                    ContextCompat.getDrawable(this, R.drawable.in_favorites)?.constantState?.hashCode()
                ) {
                    imageView.setImageDrawable(getDrawable(R.drawable.not_in_favorites))

                    val mutableSmartphonesId = fav.smartphonesId.toMutableList()
                    mutableSmartphonesId.removeAt(mutableSmartphonesId.indexOf(smartphoneId))
                    fav.smartphonesId = mutableSmartphonesId.toList()

                } else {
                    imageView.setImageDrawable(getDrawable(R.drawable.in_favorites))
                    val mutableSmartphonesId = fav.smartphonesId.toMutableList()
                    mutableSmartphonesId.add(smartphoneId)
                    fav.smartphonesId = mutableSmartphonesId.toList()
                }

                db.collection("favorites").document(unicId)
                    .update(fav.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {

            }
    }

    fun  showSmartphoneInformationPage(ean: String?){
        val intent = Intent(this, SmartphoneInfoActivity::class.java)
        intent.putExtra("uidSmartphone", ean)
        startActivity(intent)
        finish()
    }
}
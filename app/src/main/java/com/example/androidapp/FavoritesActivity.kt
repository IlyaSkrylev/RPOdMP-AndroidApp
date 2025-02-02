package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.example.androidapp.databinding.ActivityFavoritesBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FavoritesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityFavoritesBinding

    private var userEmail: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        this.binding = ActivityFavoritesBinding.inflate(layoutInflater)
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

        this.userEmail = user?.email
        this.userId = user?.uid

        this.binding.backLabel.setOnClickListener{ showMainPage() }
        this.binding.backIcon.setOnClickListener{ showMainPage() }

        showUserFavorites()
    }

    private fun showMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showUserFavorites() {
        if (userId == null || userId == "" || userEmail == null || userEmail == "") return

        db = FirebaseFirestore.getInstance()
        db.collection("favorites").whereEqualTo("userId", userId).limit(1).get()
            .addOnSuccessListener { favorite ->
                if (!favorite.isEmpty) {
                    val document = favorite.first()
                    val favoriteInfo = document.toObject(Favorite::class.java)

                    for (smartphoneId in favoriteInfo.smartphonesId) {
                        db.collection("smartphones").whereEqualTo("ean", smartphoneId).limit(1).get()
                            .addOnSuccessListener { smartphone ->
                                if (!smartphone.isEmpty){
                                    val documentSmartphone = smartphone.first()
                                    showSmartphoneItem(documentSmartphone.toObject(Smartphone::class.java), favoriteInfo)
                                } else {

                                }
                            }
                    }
                }else {

                }
            }.addOnFailureListener{

            }

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun showSmartphoneItem(smartphoneInfo: Smartphone, favorite: Favorite) {
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
        fav.setOnClickListener{ addOrDeleteFromFavorites(fav, favorite, smartphoneInfo.ean) }
        fav.setImageDrawable(getDrawable(R.drawable.in_favorites))

        val item = view.findViewById<FrameLayout>(R.id.item)
        item.setOnClickListener{ showSmartphoneInformationPage(smartphoneInfo.ean) }

        this.binding.smartphones.addView(view)
    }

    private fun showSmartphoneInformationPage(ean: String?) {
        val intent = Intent(this, SmartphoneInfoActivity::class.java)
        intent.putExtra("uidSmartphone", ean)
        startActivity(intent)
        finish()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addOrDeleteFromFavorites(fav: ImageView?, favorite: Favorite, smartphoneId: String?) {
        if (userId == null || userId == "" || userEmail == null || userEmail == "" || smartphoneId == null) return

        db = FirebaseFirestore.getInstance()


        if (fav?.drawable?.constantState?.hashCode() ==
            ContextCompat.getDrawable(this, R.drawable.in_favorites)?.constantState?.hashCode()){
            fav?.setImageDrawable(getDrawable(R.drawable.not_in_favorites))

            val mutableSmartphonesId = favorite.smartphonesId.toMutableList()
            mutableSmartphonesId.removeAt(mutableSmartphonesId.indexOf(smartphoneId))
            favorite.smartphonesId = mutableSmartphonesId.toList()

        }
        else{
            fav?.setImageDrawable(getDrawable(R.drawable.in_favorites))
            val mutableSmartphonesId = favorite.smartphonesId.toMutableList()
            mutableSmartphonesId.add(smartphoneId)
            favorite.smartphonesId = mutableSmartphonesId.toList()
        }

        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val id = document.id
                    db.collection("favorites").document(id)
                        .update(favorite.toMap())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    db.collection("favorites").add(favorite.toMap())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully changed", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if(true){
            showMainPage()
        } else {
            super.onBackPressed()
        }
    }
}
package com.example.androidapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.google.firebase.firestore.QuerySnapshot

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

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

        val find = this.binding.find
        find.setOnClickListener { ShowSmartphones(this.binding.find.text.toString()) }
        find.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) { ShowSmartphones(find.text.toString()) }
        })

        val items = arrayOf(PhoneBrands.All, PhoneBrands.iPhone, PhoneBrands.Poco, PhoneBrands.Xiaomi, PhoneBrands.Honor,
            PhoneBrands.Huawei, PhoneBrands.Samsung, PhoneBrands.Pixel, PhoneBrands.HTC, PhoneBrands.Motorola)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.binding.spinnerBrands.adapter = adapter
        this.binding.spinnerBrands.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        val layoutParams = this.binding.filter.layoutParams
        layoutParams.height = 0
        this.binding.filter.layoutParams = layoutParams

        this.binding.filterButton.setOnClickListener{ showOrHideFilter() }
        this.binding.filterSearch.setOnClickListener{ searchFilteredPhones() }

        ShowSmartphones("")
    }

    private fun searchFilteredPhones() {
        val userId = user?.uid
        if (userId == "" || userId == null) return
        val brand = if (this.binding.spinnerBrands.selectedItem.toString() == "All") "" else this.binding.spinnerBrands.selectedItem.toString()

        val memFrom = if (this.binding.memoryFrom.text.toString() == "") Int.MIN_VALUE else this.binding.memoryFrom.text.toString().toInt()
        val memTo = if (this.binding.memoryTo.text.toString() == "") Int.MAX_VALUE else this.binding.memoryTo.text.toString().toInt()

        val ramFrom = if (this.binding.ramFrom.text.toString() == "") Int.MIN_VALUE else this.binding.ramFrom.text.toString().toInt()
        val ramTo = if (this.binding.ramTo.text.toString() == "") Int.MAX_VALUE else this.binding.ramTo.text.toString().toInt()

        val batteryFrom = if (this.binding.batteryFrom.text.toString() == "") Int.MIN_VALUE else this.binding.batteryFrom.text.toString().toInt()
        val batteryTo = if (this.binding.batteryTo.text.toString() == "") Int.MAX_VALUE else this.binding.batteryTo.text.toString().toInt()

        val coresFrom = if (this.binding.coresFrom.text.toString() == "") Int.MIN_VALUE else this.binding.coresFrom.text.toString().toInt()
        val coresTo = if (this.binding.coresTo.text.toString() == "") Int.MAX_VALUE else this.binding.coresTo.text.toString().toInt()

        db = FirebaseFirestore.getInstance()
        db.collection("smartphones").whereGreaterThanOrEqualTo("name", brand)
            .whereLessThan("name", brand + "\uf8ff")
            .whereGreaterThanOrEqualTo("ramMemory", ramFrom)
            .whereLessThanOrEqualTo("ramMemory", ramTo)
            .whereGreaterThanOrEqualTo("builtInMemory", memFrom)
            .whereLessThanOrEqualTo("builtInMemory", memTo)
            .whereGreaterThanOrEqualTo("batteryCapacity", batteryFrom)
            .whereLessThanOrEqualTo("batteryCapacity", batteryTo)
            .whereGreaterThanOrEqualTo("numberOfProcessorCores", coresFrom)
            .whereLessThanOrEqualTo("numberOfProcessorCores", coresTo)
            .get().addOnSuccessListener {
                    smartphones ->
                showOrHideFilter()
                this.binding.smartphones.removeAllViews()
                addSmartphoneItem(smartphones, userId)
            }
    }

    private fun showOrHideFilter() {
        val initialHeight = this.binding.filter.height
        var targetHeight = 0
        var duration : Long = 300
        if (initialHeight == 0) {
            targetHeight = ViewGroup.LayoutParams.WRAP_CONTENT
            duration = 0
        }

        var valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight)

        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            this.binding.filter.layoutParams.height = animatedValue
            this.binding.filter.requestLayout()
        }
        valueAnimator.duration = duration
        valueAnimator.start()
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

    fun ShowSmartphones() {
        val userId = user?.uid
        if (userId == "" || userId == null) return

        db = FirebaseFirestore.getInstance()
        db.collection("smartphones")
            .get()
            .addOnSuccessListener { smartphones ->
                addSmartphoneItem(smartphones, userId)
            }.addOnFailureListener {
            }
    }

    fun ShowSmartphones(filter: String) {
        val userId = user?.uid
        if (userId == "" || userId == null) return
        db = FirebaseFirestore.getInstance()
        val query = if (filter.isNotEmpty()) {
            db.collection("smartphones")
                .whereGreaterThanOrEqualTo("name", filter)
                .whereLessThan("name", filter + "\uf8ff")
        } else {
            db.collection("smartphones")
        }

        query.get()
            .addOnSuccessListener { smartphones ->
                this.binding.smartphones.removeAllViews()
                addSmartphoneItem(smartphones, userId)
            }.addOnFailureListener { exception ->
            }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "MissingInflatedId",
        "DefaultLocale"
    )
    fun addSmartphoneItem(smartphones: QuerySnapshot, userId: String?){
        for (smartphone in smartphones) {
            val smartphoneInfo = smartphone.toObject(Smartphone::class.java)

            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(
                R.layout.smatrphone_item,
                this.binding.smartphones,
                false
            )

            val phoneImage = view.findViewById<ImageView>(R.id.phone_image)
            Glide.with(this)
                .load(smartphoneInfo.base64Images[0])
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .into(phoneImage)

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

                        if (smartphoneInfo.ean in favoriteInfo.smartphonesId) {
                            fav.setImageDrawable(getDrawable(R.drawable.in_favorites))
                        }
                    }
                }

            val item = view.findViewById<FrameLayout>(R.id.item)
            item.setOnClickListener { showSmartphoneInformationPage(smartphoneInfo.ean) }

            getRating(view, smartphoneInfo.ean)

            this.binding.smartphones.addView(view)
        }
    }

    private fun getRating(view: View?, ean: String?){
        if(view ==null || ean == null) return
        var rating = 0.0;

        db = FirebaseFirestore.getInstance()
        db.collection("reviews").document(ean).collection("ratings").get()
            .addOnSuccessListener { documents ->
                var sum = 0
                var count = 0
                for (document in documents){
                    val ratingInfo = document.toObject(Rating::class.java)
                    sum += ratingInfo.rating
                    count++
                }
                if (count != 0)
                    rating = sum / count.toDouble()

                view.findViewById<TextView>(R.id.rating).setText(String.format("%.2f", rating))

                showRating(view, rating)
            }
            .addOnFailureListener{
                rating = 0.0
            }

        return
    }

    private fun showRating(view: View?, rating: Double){
        if (view == null) return
        val imgStars = listOf(
            view.findViewById<ImageView>(R.id.star_first),
            view.findViewById<ImageView>(R.id.star_second),
            view.findViewById<ImageView>(R.id.star_third),
            view.findViewById<ImageView>(R.id.star_forth),
            view.findViewById<ImageView>(R.id.star_fifth)
        )

        var i = 0
        val rait = Math.round(rating)
        for (star in imgStars){
            i++
            if (i <= rait){
                star.setImageResource(R.drawable.star_yellow)
            } else {
                star.setImageResource(R.drawable.star_black)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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

                if (!favorite.isEmpty) {
                    db.collection("favorites").document(unicId)
                        .update(fav.toMap())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    db.collection("favorites").add(fav.toMap())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
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
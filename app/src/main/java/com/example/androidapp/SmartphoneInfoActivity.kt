package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidapp.databinding.ActivitySmartphoneInfoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Date
import android.view.View;
import android.widget.FrameLayout;

class SmartphoneInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivitySmartphoneInfoBinding
    private lateinit var db: FirebaseFirestore
    private var uidSmartphone: String? = null
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    private var clickedRating: Int = 0


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

        this.binding.saveComment.setOnClickListener{ addComment() }

        val starsImg = listOf (
            this.binding.starFirst,
            this.binding.starSecond,
            this.binding.starThird,
            this.binding.starForth,
            this.binding.starFifth
        )

        this.binding.starFirst.setOnClickListener{ drawStars(starsImg,1) }
        this.binding.starSecond.setOnClickListener{ drawStars(starsImg, 2) }
        this.binding.starThird.setOnClickListener{ drawStars(starsImg,3) }
        this.binding.starForth.setOnClickListener{ drawStars(starsImg,4) }
        this.binding.starFifth.setOnClickListener{ drawStars(starsImg,5) }

        this.binding.saveRating.setOnClickListener{ sendRating(this.clickedRating) }

        showSmartphoneInfo()
        showComments()
        hideRating()
    }

    private fun showMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun showSmartphoneInfo(){
        db = FirebaseFirestore.getInstance()
        db.collection("smartphones").whereEqualTo("ean", uidSmartphone).get()
            .addOnSuccessListener { smartphones ->
                for (smartphone in smartphones) {
                    val smartphoneInfo = smartphone.toObject(Smartphone::class.java)

                    this.binding.name.setText(smartphoneInfo.name)
                    this.binding.manufacturerCode.setText(smartphoneInfo.manufacturerCode)
                    this.binding.phoneBrand.setText(smartphoneInfo.phoneBrand)
                    this.binding.phoneModel.setText(smartphoneInfo.phoneModel)
                    this.binding.ean.setText(smartphoneInfo.ean)

                    this.binding.color.setText(smartphoneInfo.color)
                    this.binding.material.setText(smartphoneInfo.material)
                    this.binding.width.setText(getNormalSize(smartphoneInfo.width, 300) + "mm")////////
                    this.binding.height.setText(getNormalSize(smartphoneInfo.height,300) + "mm")////////
                    this.binding.depth.setText(getNormalSize(smartphoneInfo.depth, 30) + "mm")///////
                    this.binding.weight.setText(getNormalSize(smartphoneInfo.weight, 500) + "g")//////////

                    this.binding.screenDiagonal.setText(getNormalSize(smartphoneInfo.screenDiagonal, 30) + "\'\'")////////
                    this.binding.displayType.setText(smartphoneInfo.displayType)
                    this.binding.touchScreen.setText(smartphoneInfo.touchScreen)
                    this.binding.pixelDensity.setText(getNormalSize(smartphoneInfo.pixelDensity, 1200) + "ppi")///////
                    this.binding.screenResolution.setText(smartphoneInfo.screenResolution)

                    this.binding.rearCameraResolution.setText(getNormalSize(smartphoneInfo.rearCameraResolution, 150) + "Mpx")////
                    this.binding.frontCameraResolution.setText(getNormalSize(smartphoneInfo.frontCameraResolution, 150) + "Mpx")////
                    this.binding.cameraFreatures.setText(smartphoneInfo.cameraFeatures)
                    this.binding.resolutionOfRecordedVideos.setText(smartphoneInfo.resolutionOfRecordedVideos)

                    this.binding.processor.setText(smartphoneInfo.processor)
                    this.binding.processor.setText(getNormalSize(smartphoneInfo.processorFrequency, 15) + "GHz")////
                    this.binding.numberOfProcessorCores.setText(getNormalSize(smartphoneInfo.numberOfProcessorCores, 20))////

                    this.binding.builtInMemory.setText(smartphoneInfo.builtInMemory.toString() + "GB")///
                    this.binding.ramMemory.setText(smartphoneInfo.ramMemory.toString() + "GB")///
                    this.binding.operatingSystem.setText(smartphoneInfo.operatingSystem)
                    this.binding.batteryCapacity.setText(smartphoneInfo.batteryCapacity.toString() + "mAh")///
                    this.binding.connectors.setText(smartphoneInfo.connectors)

                    imageSliderAdapter = ImageSliderAdapter(smartphoneInfo.base64Images)
                    this.binding.viewPager.adapter = imageSliderAdapter

                }
            }
    }

    @SuppressLint("DefaultLocale")
    private fun getNormalSize(number: Int, noLess: Int): String{
        var fNum = number.toFloat()
        var i = 0

        while(fNum > noLess){
            i++
            fNum /= 10
        }

        return String.format("%.${i}f", fNum)
    }

    override fun onBackPressed() {
        if(true){
            showMainPage()
        } else {
            super.onBackPressed()
        }
    }

    private fun addComment(){
        val userId = user?.uid
        if (userId == null || userId == "" || uidSmartphone == null || this.binding.comment.text.toString() == "") return

        val dateTime = Date()
        val date = SimpleDateFormat("dd-MM-yyyy")
        val time = SimpleDateFormat("HH:mm")
        var comment = Comment(
            date.format(dateTime),
            time.format(dateTime),
            uidSmartphone!!,
            userId,
            this.binding.comment.text.toString()
        )

        db = FirebaseFirestore.getInstance()
        db.collection("reviews").document(uidSmartphone!!).collection("comments").add(comment)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully changed", Toast.LENGTH_SHORT).show()
                this.binding.comment.setText("")
                showComments()
            }.addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    fun showComments(){
        db = FirebaseFirestore.getInstance()
        db.collection("reviews").document(uidSmartphone!!).collection("comments").get()
            .addOnSuccessListener { comments ->
                this.binding.comments.removeAllViews()
                for (comment in comments) {
                    val com = comment.toObject(Comment::class.java)

                    val inflater = LayoutInflater.from(this)
                    val view = inflater.inflate(
                        R.layout.comment_item,
                        this.binding.comments,
                        false
                    )

                    db.collection("users").whereEqualTo("uid", com.idUser).get()
                        .addOnSuccessListener { users ->
                            for (user in users) {
                                val userInfo = user.toObject(User::class.java)

                                if (userInfo.firstName != "" || userInfo.lastName != "")
                                view.findViewById<TextView>(R.id.name).setText(userInfo.firstName + " " + userInfo.lastName)

                                val decodedByteArray = Base64.decode(userInfo.avatar, Base64.DEFAULT)
                                val bitmap: Bitmap? = BitmapFactory.decodeByteArray(
                                    decodedByteArray,
                                    0,
                                    decodedByteArray.size
                                )
                                if (bitmap != null)
                                    view.findViewById<ImageView>(R.id.avatar).setImageBitmap(getCircularBitmap(bitmap))
                            }
                        }

                    view.findViewById<TextView>(R.id.date_time).setText(com.date + "   " + com.time)
                    view.findViewById<TextView>(R.id.comment).setText(com.comment)

                    val starsImg = listOf (
                        view.findViewById<ImageView>(R.id.star_first),
                        view.findViewById<ImageView>(R.id.star_second),
                        view.findViewById<ImageView>(R.id.star_third),
                        view.findViewById<ImageView>(R.id.star_forth),
                        view.findViewById<ImageView>(R.id.star_fifth),
                    )

                    db.collection("reviews").document(uidSmartphone!!).collection("ratings")
                        .whereEqualTo("userId", com.idUser).limit(1).get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val document = querySnapshot.documents[0]
                                val ratingInfo = document.toObject(Rating::class.java)

                                if (ratingInfo != null) {
                                    drawStars(starsImg, ratingInfo.rating)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            drawStars(starsImg, 0)
                        }

                    this.binding.comments.addView(view)
                }
            }
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)

        val circularBitmap = Bitmap.createBitmap(squaredBitmap.width, squaredBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circularBitmap)

        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val radius = squaredBitmap.width / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        squaredBitmap.recycle()

        return circularBitmap
    }

    fun drawStars(starsImg: List<ImageView>, clickedStar: Int){
        this.clickedRating = clickedStar

        var i = 0;
        for (star in starsImg){
            i++
            if (i <= clickedStar){
                star.setImageResource(R.drawable.star_yellow)
            } else {
                star.setImageResource(R.drawable.star_black)
            }
        }
    }

    fun sendRating(rating: Int){
        val userId = user?.uid
        if (userId == null || uidSmartphone == null) return

        if (rating == 0){
            Toast.makeText(this, "Rate the selected smartphone", Toast.LENGTH_SHORT).show()
            return
        }
        val data = Rating(
            userId,
            uidSmartphone!!,
            rating
        )

        db = FirebaseFirestore.getInstance()
        db.collection("reviews").document(uidSmartphone!!).collection("ratings").add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Rating recorded successfully", Toast.LENGTH_SHORT).show()
                val fl = this.binding.rating
                fl.visibility = View.INVISIBLE
            }
            .addOnFailureListener{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    fun hideRating(){
        val userId = user?.uid
        if (userId == null || uidSmartphone == null) return

        db = FirebaseFirestore.getInstance()
        db.collection("reviews").document(uidSmartphone!!).collection("ratings")
            .whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    this.binding.rating.visibility = View.INVISIBLE
                }
            }
    }
}
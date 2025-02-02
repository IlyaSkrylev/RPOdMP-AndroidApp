package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.databinding.ActivitySmartphoneInfoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SmartphoneInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivitySmartphoneInfoBinding
    private lateinit var db: FirebaseFirestore
    private var uidSmartphone: String? = null
    private lateinit var imageSliderAdapter: ImageSliderAdapter


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

    @SuppressLint("SetTextI18n")
    fun showSmartphoneInfo(){
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
    fun getNormalSize(number: Int, noLess: Int): String{
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
}
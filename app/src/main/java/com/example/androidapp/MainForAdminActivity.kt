package com.example.androidapp

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.databinding.ActivityMainForAdminBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainForAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivityMainForAdminBinding
    private lateinit var db: FirebaseFirestore

    private lateinit var adapter: ImageSliderAdapter
    private val selectedImages = mutableListOf<ByteArray>()

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
        this.binding = ActivityMainForAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        this.binding.logoutIcon.setOnClickListener { logOut() }
        this.binding.logoutLabel.setOnClickListener { logOut() }
        //this.binding.backIcon.setOnClickListener { showMainPage() }
        //this.binding.backLabel.setOnClickListener { showMainPage() }
        this.binding.createButton.setOnClickListener { createSmartphone() }
        this.binding.chooseImages.setOnClickListener { chooseImages() }

    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createSmartphone() {
        val base64Images: MutableList<String> = mutableListOf()
        for (image in selectedImages) {
            val base64String = Base64.encodeToString(image, Base64.DEFAULT)
            base64Images.add(base64String)
        }
        val urls = this.binding.etImages.text.toString().split("\n")
        for (url in urls){
            base64Images.add(url)
        }

        val smartphoneInfo = Smartphone(
            this.binding.etName.text.toString(),
            this.binding.etManufacturerCode.text.toString(),
            this.binding.etPhoneBrand.text.toString(),
            this.binding.etPhoneModel.text.toString(),
            this.binding.etEan.text.toString(),
            this.binding.etColor.text.toString(),
            this.binding.etMaterial.text.toString(),
            this.binding.etWidth.text.toString().toInt(),
            this.binding.etHeight.text.toString().toInt(),
            this.binding.etDepth.text.toString().toInt(),
            this.binding.etWeight.text.toString().toInt(),
            this.binding.etScreenDiagonal.text.toString().toInt(),
            this.binding.etDisplayType.text.toString(),
            this.binding.etTouchScreen.text.toString(),
            this.binding.etPixelDensity.text.toString().toInt(),
            this.binding.etScreenResolution.text.toString(),
            this.binding.etRearCameraResolution.text.toString().toInt(),
            this.binding.etFrontCameraResolution.text.toString().toInt(),
            this.binding.etCameraFeatures.text.toString(),
            this.binding.etResolutionOfRecordedVideos.text.toString(),
            this.binding.etProcessor.text.toString(),
            this.binding.etProcessorFrequency.text.toString().toInt(),
            this.binding.etNumberOfProcessorCores.text.toString().toInt(),
            this.binding.etBuiltInMemory.text.toString().toInt(),
            this.binding.etRamMemory.text.toString().toInt(),
            this.binding.etOperatingSystem.text.toString(),
            this.binding.etBatteryCapacity.text.toString().toInt(),
            this.binding.etConnectors.text.toString(),
            base64Images
        )

        db = FirebaseFirestore.getInstance()
        db.collection("smartphones").add(smartphoneInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully changed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun chooseImages() {
        selectedImages.clear()
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_IMAGES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            data?.let { intentData ->
                if (intentData.clipData != null) {
                    val count = intentData.clipData!!.itemCount
                    for (i in 0 until count) {
                        val uri = intentData.clipData!!.getItemAt(i).uri
                        val byteArray = contentResolver.openInputStream(uri)?.readBytes()
                        byteArray?.let { selectedImages.add(it) }
                    }
                } else {
                    intentData.data?.let { uri ->
                        val byteArray = contentResolver.openInputStream(uri)?.readBytes()
                        byteArray?.let { selectedImages.add(it) }
                    }
                }
                adapter = ImageSliderAdapter(selectedImages)
                this.binding.viewPager.adapter = adapter
            }
        }
    }

    companion object {
        private const val PICK_IMAGES = 1
    }

}
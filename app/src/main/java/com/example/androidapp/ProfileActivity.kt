package com.example.androidapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import android.util.Base64
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivityProfileBinding
    //private lateinit var database: DatabaseReference
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
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
        this.binding = ActivityProfileBinding.inflate(layoutInflater)
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
        this.binding.backIcon.setOnClickListener { showMainPage() }
        this.binding.backLabel.setOnClickListener { showMainPage() }
        this.binding.saveButton.setOnClickListener { saveChanges() }

        this.binding.userAvatar.setOnClickListener{ setAvatar() }

        this.binding.firstName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.firstName, this.binding.tvFirstName)
            else returnTextEditBackground(this.binding.firstName, this.binding.tvFirstName)
        }
        this.binding.lastName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.lastName, this.binding.tvLastName)
            else returnTextEditBackground(this.binding.lastName, this.binding.tvLastName)
        }
        this.binding.patronymic.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.patronymic, this.binding.tvPatronymic)
            else returnTextEditBackground(this.binding.patronymic, this.binding.tvPatronymic)
        }

        this.binding.birthDate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showCalendar()
                setBlueBorders(this.binding.birthDate, this.binding.tvBirthDate)
            } else returnTextEditBackground(this.binding.birthDate, this.binding.tvBirthDate)
        }
        this.binding.birthDate.setOnClickListener { showCalendar() }

        this.binding.genderBack.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setViewBlueBorders(this.binding.genderBack, this.binding.tvGender)
            else returnViewBackground(this.binding.genderBack, this.binding.tvGender)
        }
        this.binding.telephoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(
                this.binding.telephoneNumber,
                this.binding.tvTelephoneNumber
            )
            else returnTextEditBackground(
                this.binding.telephoneNumber,
                this.binding.tvTelephoneNumber
            )
        }
        this.binding.country.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.country, this.binding.tvCountry)
            else returnTextEditBackground(this.binding.country, this.binding.tvCountry)
        }
        this.binding.city.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.city, this.binding.tvCity)
            else returnTextEditBackground(this.binding.city, this.binding.tvCity)
        }
        this.binding.description.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.description, this.binding.tvDescription)
            else returnTextEditBackground(this.binding.description, this.binding.tvDescription)
        }

        val items = arrayOf(Gender.None, Gender.Male, Gender.Female, Gender.Other)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.binding.spinnerGender.adapter = adapter
        this.binding.spinnerGender.onItemSelectedListener =
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

        ShowUserInfo()
    }

    private fun logOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveChanges() {
        val userId = user?.uid
        if(userId == null) return

        val img = getImageByteArray(this.binding.userAvatar)
        val firstName = this.binding.firstName.text.toString()
        val lastName = this.binding.lastName.text.toString()
        val patronymic = this.binding.patronymic.text.toString()
        val birthDate = this.binding.birthDate.text.toString()
        val sex = this.binding.spinnerGender.selectedItem.toString()
        val telNumber = this.binding.telephoneNumber.text.toString()
        val country = this.binding.country.text.toString()
        val city = this.binding.city.text.toString()
        val description = this.binding.description.text.toString()

        val userInfo = User(
            user?.email.toString(),
            userId.toString(),
            firstName,
            lastName,
            patronymic,
            birthDate,
            sex,
            telNumber,
            country,
            city,
            description,
            Base64.encodeToString(img, Base64.DEFAULT)
            )

        db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("uid", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val id = document.id
                    db.collection("users").document(id)
                        .update(userInfo.toMap())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    db.collection("users").add(userInfo.toMap())
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

    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, R.style.CustomDatePickerDialog,
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker?,
                    selectedYear: Int,
                    selectedMonth: Int,
                    selectedDay: Int
                ) {
                    val formattedDate =
                        String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    binding.birthDate.setText(formattedDate)
                }
            }, year, month, day
        )

        datePickerDialog.show()
    }

    fun setBlueBorders(editText: EditText, textView: TextView) {
        textView.setTextColor(getColor(R.color.blue))
        editText.background = getDrawable(R.drawable.edit_text_blue_borders)
    }

    fun returnTextEditBackground(editText: EditText, textView: TextView) {
        textView.setTextColor(getColor(R.color.black))
        editText.background = getDrawable(R.drawable.edit_text_background)
    }

    fun setViewBlueBorders(view: View, textView: TextView) {
        textView.setTextColor(getColor(R.color.blue))
        view.background = getDrawable(R.drawable.view_blue_borders)
    }

    fun returnViewBackground(view: View, textView: TextView) {
        textView.setTextColor(getColor(R.color.black))
        view.background = getDrawable(R.drawable.view_gray_borders)
    }

    private fun getImageByteArray(imageView: ImageView): ByteArray? {
        val drawable: Drawable? = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap: Bitmap = drawable.bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
        return null
    }

    private fun ShowUserInfo() {
        val userId = user?.uid.toString()
        if (userId == null || userId == "") return

        db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("uid", userId).get()
            .addOnSuccessListener{ documents ->
                for (document in documents){
                    val userInfo = document.toObject(User::class.java)

                    binding.firstName.setText(userInfo.firstName)
                    binding.lastName.setText(userInfo.lastName)
                    binding.patronymic.setText(userInfo.patronymic)
                    binding.birthDate.setText(userInfo.birthDate)
                    when (userInfo.sex) {
                        "none" -> binding.spinnerGender.setSelection(0)
                        "Male" -> binding.spinnerGender.setSelection(1)
                        "Female" -> binding.spinnerGender.setSelection(2)
                        else -> binding.spinnerGender.setSelection(3)
                    }
                    binding.telephoneNumber.setText(userInfo.telephoneNumber)
                    binding.country.setText(userInfo.country)
                    binding.city.setText(userInfo.city)
                    binding.description.setText(userInfo.description)

                    val decodedByteArray = Base64.decode(userInfo.avatar, Base64.DEFAULT)
                    val bitmap: Bitmap? = BitmapFactory.decodeByteArray(
                        decodedByteArray,
                        0,
                        decodedByteArray.size
                    )
                    if (bitmap != null)
                        binding.userAvatar.setImageBitmap(getCircularBitmap(bitmap))
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


    fun setAvatar(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            binding.userAvatar.setImageURI(imageUri)
            val bytes = getImageByteArray(binding.userAvatar)
            val bitmap: Bitmap? = bytes?.let {
                BitmapFactory.decodeByteArray(
                    bytes,
                    0,
                    it.size
                )
            }
            if (bitmap != null)
                binding.userAvatar.setImageBitmap(getCircularBitmap(bitmap))

        }
    }

    fun User.toMap(): Map<String, Any> {
        return mapOf(
            "email" to email,
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "patronymic" to patronymic,
            "birthDate" to birthDate,
            "sex" to sex,
            "telNumber" to telephoneNumber,
            "country" to country,
            "city" to city,
            "description" to description,
            "avatar" to avatar
        )
    }
}

package com.example.androidapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
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

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: DatabaseReference

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

        this.binding.logoutIcon.setOnClickListener{ logOut() }
        this.binding.logoutLabel.setOnClickListener{ logOut() }
        this.binding.backIcon.setOnClickListener{ showMainPage() }
        this.binding.backLabel.setOnClickListener{ showMainPage() }
        this.binding.saveButton.setOnClickListener{ saveChanges() }

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

        this.binding.birthDate.setOnFocusChangeListener{ view, hasFocus ->
            if (hasFocus) {
                showCalendar()
                setBlueBorders(this.binding.birthDate, this.binding.tvBirthDate)
            }
            else returnTextEditBackground(this.binding.birthDate, this.binding.tvBirthDate)
        }
        this.binding.birthDate.setOnClickListener{ showCalendar() }

        this.binding.genderBack.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setViewBlueBorders(this.binding.genderBack, this.binding.tvGender)
            else returnViewBackground(this.binding.genderBack, this.binding.tvGender)
        }
        this.binding.telephoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) setBlueBorders(this.binding.telephoneNumber, this.binding.tvTelephoneNumber)
            else returnTextEditBackground(this.binding.telephoneNumber, this.binding.tvTelephoneNumber)
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
        this.binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
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

    private fun showCalendar(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, R.style.CustomDatePickerDialog,
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
                    val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    binding.birthDate.setText(formattedDate)
                }
            }, year, month, day)

        datePickerDialog.show()
    }

    fun setBlueBorders(editText: EditText, textView: TextView){
        textView.setTextColor(getColor(R.color.blue))
        editText.background = getDrawable(R.drawable.edit_text_blue_borders)
    }

    fun returnTextEditBackground (editText: EditText, textView: TextView){
        textView.setTextColor(getColor(R.color.black))
        editText.background = getDrawable(R.drawable.edit_text_background)
    }

    fun setViewBlueBorders(view: View, textView: TextView){
        textView.setTextColor(getColor(R.color.blue))
        view.background = getDrawable(R.drawable.view_blue_borders)
    }

    fun returnViewBackground(view: View, textView: TextView){
        textView.setTextColor(getColor(R.color.black))
        view.background = getDrawable(R.drawable.view_gray_borders)
    }
}
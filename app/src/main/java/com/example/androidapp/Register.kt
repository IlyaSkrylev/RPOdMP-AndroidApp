package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var tvBack: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepeatedPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvErrEmail: TextView
    private lateinit var tvErrPassword: TextView
    private lateinit var tvErrRepeatedPassword: TextView
    private lateinit var ivShowPassword: ImageView
    private lateinit var ivShowRepeatedPassword: ImageView

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.ivBack = findViewById(R.id.back_button)
        this.tvBack = findViewById(R.id.back_text)
        this.etEmail = findViewById(R.id.email)
        this.etPassword = findViewById(R.id.password)
        this.etRepeatedPassword = findViewById(R.id.repeat_password)
        this.tvErrEmail = findViewById(R.id.error_email)
        this.tvErrPassword = findViewById(R.id.error_password)
        this.tvErrRepeatedPassword = findViewById(R.id.error_repeated_password)
        this.btnRegister = findViewById(R.id.register_button)
        this.ivShowPassword = findViewById(R.id.show_password)
        this.ivShowRepeatedPassword = findViewById(R.id.show_repeated_password)
        this.auth = Firebase.auth

        this.btnRegister.setOnClickListener{
            if (CheckEnteredData()) {
                CreateAccount()
            }
        }

        this.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) returnTextEditBackground(etEmail)
            if (hasFocus) setBlueBorders(etEmail, tvErrEmail)
        }
        this.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) returnTextEditBackground(etPassword)
            if (hasFocus) setBlueBorders(etPassword, tvErrPassword)
        }
        this.etRepeatedPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) returnTextEditBackground(etRepeatedPassword)
            if (hasFocus) setBlueBorders(etRepeatedPassword, tvErrRepeatedPassword)
        }

        this.etEmail.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) { s?.let{ onTextBoxChange(it, etEmail, tvErrEmail, true) } }
        })
        this.etPassword.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) { s?.let{ onTextBoxChange(it, etPassword, tvErrPassword, false) } }
        })
        this.etRepeatedPassword.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) { s?.let{ onTextBoxChange(it, etRepeatedPassword, tvErrRepeatedPassword, false) } }
        })

        this.ivShowPassword.setOnClickListener{ ShowEditTextData(etPassword, ivShowPassword) }
        this.ivShowRepeatedPassword.setOnClickListener{ ShowEditTextData(etRepeatedPassword, ivShowRepeatedPassword) }
        this.tvBack.setOnClickListener{ showLoginPage() }
        this.ivBack.setOnClickListener{ showLoginPage() }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun CheckEnteredData(): Boolean{
        val email = InputValidation(this, etEmail, tvErrEmail)
        val password = InputValidation(this, etPassword, tvErrPassword)
        val repeatedPassword = InputValidation(this, etRepeatedPassword, tvErrRepeatedPassword)

        if (email.isEmptyField()){
            email.ShowIncorectEnter(getString(R.string.err_empty_field))
            return false
        }

        if (password.isEmptyField()){
            password.ShowIncorectEnter(getString(R.string.err_empty_field))
            return false
        }

        if (repeatedPassword.isEmptyField()){
            repeatedPassword.ShowIncorectEnter(getString(R.string.err_empty_field))
            return false
        }

        if (!email.isValidEmail()){
            email.ShowIncorectEnter((getString((R.string.err_incorrect_email_format))))
            return false
        }

        if (!password.isCorrectPassword()){
            password.ShowIncorectEnter(getString(R.string.err_small_password))
            return false
        }

        if (!password.isPasswordsEquals(repeatedPassword)) {
            repeatedPassword.ShowIncorectEnter(getString(R.string.err_passwords_must_match))
            return false
        }
        return true
    }

    fun setBlueBorders(editText: EditText, tvErr: TextView){
        val params = tvErr.layoutParams
        params.height = 0
        tvErr.layoutParams = params

        editText.background = getDrawable(R.drawable.edit_text_blue_borders)
    }

    fun onTextBoxChange(editable: Editable, editText: EditText, tvErr: TextView, isEmail : Boolean){
        setBlueBorders(editText, tvErr)
        if (editable.isNotEmpty()) {
            val lastChar = editable.last()
            if (!(lastChar.isDigit() || lastChar.toString().matches(Regex("[a-zA-Z]")) ||
                      (isEmail && (lastChar == '.' || lastChar == '@' || lastChar == '_' ||
                                lastChar == '%' || lastChar == '+' || lastChar == '-')))){
                editable.delete(editable.length - 1, editable.length)
            }
        }
    }

    fun returnTextEditBackground (editText: EditText){
        editText.background = getDrawable(R.drawable.edit_text_background)
    }

    fun ShowEditTextData(editText: EditText, imageView: ImageView){
        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(R.drawable.icon_hide_eye)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(R.drawable.icon_show_eye)
        }

        editText.setSelection(editText.text.length)
    }

    fun CreateAccount(){
        auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Successful registration.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Main::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun showLoginPage(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
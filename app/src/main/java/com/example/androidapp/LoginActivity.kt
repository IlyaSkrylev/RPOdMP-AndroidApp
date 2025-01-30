package com.example.androidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnLogin: Button
    private lateinit var tvErrEmail : TextView
    private lateinit var tvErrPassword : TextView
    private lateinit var ivShowPassword: ImageView
    private lateinit var tvRegister: TextView
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.etEmail = findViewById(R.id.email)
        this.etPassword = findViewById(R.id.password)
        this.tvErrEmail = findViewById(R.id.error_email)
        this.tvErrPassword = findViewById(R.id.error_password)
        this.btnLogin = findViewById(R.id.login_button)
        this.ivShowPassword = findViewById(R.id.show_password)
        this.tvRegister = findViewById(R.id.register_link)
        this.auth = Firebase.auth
        //this.auth.signOut()

        this.btnLogin.setOnClickListener{
            if (CheckEnteredData()) {
                AuthorizeUser()
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

        this.ivShowPassword.setOnClickListener{ ShowEditTextData( etPassword, ivShowPassword) }
        this.tvRegister.setOnClickListener{ onClickRegister() }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun CheckEnteredData(): Boolean{
        val email = InputValidation(this, etEmail, tvErrEmail)
        val password = InputValidation(this, etPassword, tvErrPassword)

        if (email.isEmptyField()){
            email.ShowIncorectEnter(getString(R.string.err_empty_field))
            return false
        }

        if (password.isEmptyField()){
            password.ShowIncorectEnter(getString(R.string.err_empty_field))
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

    fun onClickRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun AuthorizeUser(){
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Successful authorization.", Toast.LENGTH_SHORT).show()

                    if (etEmail.text.toString() == "admin@gmail.com"){
                        val intent = Intent(this, MainForAdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
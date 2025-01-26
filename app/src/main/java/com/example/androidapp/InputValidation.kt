package com.example.androidapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class InputValidation(private val context: Context, val etField: EditText, val tvErr: TextView) {

    fun isEmptyField(): Boolean{
        if (etField.text.toString().isEmpty()) {
            ShowIncorectEnter(context.getString(R.string.err_empty_field))
            return true
        }
        return false
    }

    fun isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(etField.text.toString()).matches()
    }

    fun isCorrectPassword(): Boolean{
        if (etField.text.toString().length <= 7)
            return false
        return true
    }

    fun isPasswordsEquals(repeatedPassword: InputValidation): Boolean{
        if (etField.text.toString().equals(repeatedPassword.etField.text.toString()))
            return true
        return false
    }

    fun ShowIncorectEnter(errorText: String){
        tvErr.visibility = View.VISIBLE
        tvErr.text = errorText

        val params = tvErr.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        tvErr.layoutParams = params

        etField.background = context.getDrawable(R.drawable.edit_text_red_borders)
    }
}
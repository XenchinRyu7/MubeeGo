package com.saefulrdevs.mubeego.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditTextPassword : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                val parentLayout = parent.parent as? TextInputLayout
                val errorMessage = validatePassword(password)

                if (errorMessage != null) {
                    parentLayout?.error = errorMessage
                    parentLayout?.helperText = null
                    parentLayout?.isHelperTextEnabled = false
                } else {
                    parentLayout?.error = null
                    parentLayout?.helperText = "Password valid"
                    parentLayout?.isHelperTextEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        post {
            val parentLayout = parent.parent as? TextInputLayout
            parentLayout?.helperText = "Password harus minimal 8 karakter, mengandung angka dan huruf kapital"
            parentLayout?.isHelperTextEnabled = true
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password tidak boleh kosong"
            password.length < 8 -> "Password minimal 8 karakter"
            !password.any { it.isDigit() } -> "Password harus mengandung angka"
            !password.any { it.isUpperCase() } -> "Password harus mengandung huruf kapital"
            else -> null
        }
    }
}

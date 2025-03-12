package com.saefulrdevs.mubeego.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import com.saefulrdevs.mubeego.R

class CustomEditTextEmail : AppCompatEditText {

    private var textInputLayout: TextInputLayout? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parentLayout = parent?.parent
                if (parentLayout is TextInputLayout) {
                    textInputLayout = parentLayout
                }

                validateInput(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun validateInput(input: String) {
        when {
            input.isEmpty() -> {
                textInputLayout?.error = context.getString(R.string.empty_email_error)
                textInputLayout?.isErrorEnabled = true
            }

            !Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                textInputLayout?.error = context.getString(R.string.invalid_format_email)
                textInputLayout?.isErrorEnabled = true
            }

            else -> {
                textInputLayout?.error = null
                textInputLayout?.isErrorEnabled = false
            }
        }
    }
}

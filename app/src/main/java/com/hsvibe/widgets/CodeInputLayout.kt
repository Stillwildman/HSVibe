package com.hsvibe.widgets

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hsvibe.AppController
import com.hsvibe.R
import kotlin.math.min

/**
 * Created by Vincent on 2022/5/9.
 */
class CodeInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), TextWatcher, View.OnKeyListener {

    private var codeNumber = 0
    private var codeWidth = 0
    private var codeHeight = 0
    private var codeDivideWidth = 0
    private var state: Int = STATE_NORMAL


    private lateinit var container: LinearLayout
    private lateinit var textViews: Array<TextView>
    private lateinit var editCode: EditText

    private var onInputChangeListener: OnInputChangeListener? = null

    init {
        initAttrs(attrs)
        initViews()
        initTextView()
        initListener()
    }

    private fun initViews() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        container = LinearLayout(context).apply {
            layoutParams = params
            orientation = LinearLayout.HORIZONTAL
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            dividerDrawable = GradientDrawable().apply {
                setSize(codeDivideWidth.takeIf { it > 0 } ?: 0, 0)
            }
        }
        addView(container)
        editCode = EditText(context).apply {
            layoutParams = params
            isCursorVisible = false
            inputType = EditorInfo.TYPE_CLASS_NUMBER
            setBackgroundResource(android.R.color.transparent)
        }
        addView(editCode)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.CodeInputLayout)
        codeNumber = attr.getInt(R.styleable.CodeInputLayout_codeNumber, -1)
        codeWidth = attr.getDimensionPixelSize(R.styleable.CodeInputLayout_codeWidth, -1)
        codeHeight = attr.getDimensionPixelSize(R.styleable.CodeInputLayout_codeHeight, -1)
        codeDivideWidth = attr.getDimensionPixelSize(R.styleable.CodeInputLayout_codeDivideWidth, 0)
        attr.recycle()
    }

    private fun initListener() {
        editCode.addTextChangedListener(this)
        editCode.setOnKeyListener(this)
    }

    private fun initTextView() {
        if (codeNumber <= 0) return

        val textSize = AppController.getAppContext().resources.getDimension(R.dimen.font_text_size_xl)
        val padding = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m)
        val measuredWidth = container.measuredWidth
        val height = (measuredWidth - codeDivideWidth * (codeNumber - 1)) / codeNumber

        textViews = Array(codeNumber) { TextView(context) }

        container.removeAllViews()

        textViews.forEach { textView ->
            if (codeWidth != -1 && codeHeight != -1) {
                textView.width = codeWidth
                textView.height = codeHeight
            }
            else {
                textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT + codeDivideWidth, height, 1f)
            }
            setState(state)
            textView.setTypeface(null, Typeface.BOLD)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            textView.setPadding(padding, padding, padding, padding)
            textView.gravity = Gravity.CENTER
            textView.isFocusable = false
            textView.transformationMethod = PasswordTransformationMethod.getInstance()
            container.addView(textView)
        }
        editCode.height = container.measuredHeight
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        val code = s.toString()

        if (code.length > 1) {
            setCode(code)
        } else {
            addCode(code)
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            onInputChangeListener?.onDelete()
            deleteCode()
            return true
        }
        return false
    }

    fun focusAndShowKeyboard() {
        editCode.postDelayed({
            if (editCode.requestFocus()) {
                (AppController.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.run {
                    showSoftInput(editCode, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }, 100) // make sure that editText always show keyboard correctly
    }

    private fun addCode(code: String) {
        if (code.isEmpty() || code.length > textViews.size) {
            return
        }
        for (i in textViews.indices) {
            val textView = textViews[i]
            if (textView.text.toString().isEmpty()) {
                textView.text = code
                if (i == textViews.lastIndex) {
                    onInputChangeListener?.onComplete(getCode())
                }
                break
            }
        }
        editCode.setText("")
    }

    private fun deleteCode() {
        for (i in textViews.indices.reversed()) {
            val textView = textViews[i]
            if (textView.text.toString().isNotEmpty()) {
                textView.text = ""
                break
            }
        }
    }

    fun getCode(): String {
        val sb = StringBuilder()
        for (textView in textViews) {
            sb.append(textView.text.toString())
        }
        return sb.toString()
    }

    fun setCode(code: String) {
        if (code.isEmpty()) {
            clear()
            return
        }
        for (i in 0 until min(code.length, textViews.size)) {
            val textView = textViews[i]
            textView.text = code[i].toString()
            if (i == textViews.lastIndex) {
                onInputChangeListener?.onComplete(getCode())
            }
        }
        editCode.setText("")
    }

    fun clear() {
        for (i in textViews.indices) {
            val textView = textViews[i]
            textView.text = ""
        }
        editCode.setText("")
    }

    fun setOnInputChangeListener(listener: OnInputChangeListener) {
        onInputChangeListener = listener
    }

    fun setState(state: Int) {
        this.state = state
        when (state) {
            STATE_NORMAL -> {
                textViews.forEach { textView ->
                    textView.setTextColor(ContextCompat.getColor(AppController.getAppContext(), R.color.md_grey_900))
                    textView.setBackgroundResource(R.drawable.background_code_input_normal)
                }
            }
            STATE_ERROR -> {
                textViews.forEach { textView ->
                    textView.setTextColor(ContextCompat.getColor(AppController.getAppContext(), R.color.md_red_A400))
                    textView.setBackgroundResource(R.drawable.background_code_input_error)
                }
            }
            else -> {
                textViews.forEach { textView ->
                    textView.setTextColor(ContextCompat.getColor(AppController.getAppContext(), R.color.md_grey_900))
                    textView.setBackgroundResource(R.drawable.background_code_input_normal)
                }
            }
        }
    }

    interface OnInputChangeListener {
        fun onComplete(code: String)
        fun onDelete()
    }

    companion object {
        const val STATE_NORMAL = 0x00000000
        const val STATE_ERROR = 0x00000001
    }
}
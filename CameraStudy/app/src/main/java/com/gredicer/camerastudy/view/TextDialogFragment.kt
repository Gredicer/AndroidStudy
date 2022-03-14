package com.gredicer.schidentityapp.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.gredicer.camerastudy.databinding.FragmentTextDialogBinding

class TextDialogFragment(context: Context) : DialogFragment() {
    var mTitle = "提示"
    var mContent = "内容"
    var mAvatar: Bitmap? = null
    private var mListener: View.OnClickListener? = null

    init {
        val fragmentActivity = context as FragmentActivity
        mFragment = fragmentActivity.supportFragmentManager
    }

    companion object {
        const val BindDialogTAG = "TextDialogFragment"
        private var mFragment: FragmentManager? = null
    }


    private lateinit var binding: FragmentTextDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTextDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnEnter.setOnClickListener {
            mListener?.onClick(it)
            dialog?.dismiss()
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.apply {
            window?.apply {
                attributes.run {
                    width = WindowManager.LayoutParams.WRAP_CONTENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                setCancelable(true)
                // 设置点击dialog外的时候dialog消失
                setCanceledOnTouchOutside(false)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    fun setTitle(title: String): TextDialogFragment {
        mTitle = title
        return this
    }

    fun setContent(content: String): TextDialogFragment {
        mContent = content
        return this
    }

    fun setAvatar(content: Bitmap): TextDialogFragment {
        mAvatar = content
        return this
    }


    fun setButtonClickListener(listener: View.OnClickListener): TextDialogFragment {
        mListener = listener
        return this
    }

    fun show() {
        super.show(mFragment!!, BindDialogTAG)
    }
}
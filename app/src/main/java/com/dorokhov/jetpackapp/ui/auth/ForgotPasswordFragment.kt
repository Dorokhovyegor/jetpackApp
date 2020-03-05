package com.dorokhov.jetpackapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.DataStateChangeListener
import com.dorokhov.jetpackapp.ui.Response
import com.dorokhov.jetpackapp.ui.ResponseType
import com.dorokhov.jetpackapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForgotPasswordFragment : BaseAuthFragment(), DataStateChangeListener {

    val TAG = this.javaClass.canonicalName

    lateinit var webView: WebView

    lateinit var dataStateChangeListener: DataStateChangeListener

    val webInteractionCallBack = object : WebAppInterface.OnWebInteractionCallBack {
        override fun onSuccess(email: String) {
            onPasswordResentLinkSent()
        }

        override fun onError(errorMessage: String) {
            dataStateChangeListener.onDataStateChange(
                DataState.error<Any>(
                    Response(errorMessage, ResponseType.Dialog())
                )
            )
        }

        override fun onLoading(isLoading: Boolean) {
            // метод сидит в бекграунде, поэтому билдим DataState в Main
            GlobalScope.launch(Main) {
                dataStateChangeListener.onDataStateChange(DataState.loading(isLoading, null))
            }
        }
    }

    private fun onPasswordResentLinkSent() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()

            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )

            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        loadPasswordResetWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPasswordResetWebView() {
        dataStateChangeListener.onDataStateChange(
            DataState.loading(true, null)
        )

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                dataStateChangeListener.onDataStateChange(
                    DataState.loading(false, null)
                )
            }
        }

        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallBack),
            "AndroidTextListener"
        )
    }

    override fun onDataStateChange(dataState: DataState<*>?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "${context} must implement DataStateChangeListener")
        }
    }

    class WebAppInterface
    constructor(
        private val callback: OnWebInteractionCallBack
    ) {

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallBack {
            fun onSuccess(email: String)

            fun onError(errorMessage: String)

            fun onLoading(isLoading: Boolean)
        }

    }

}

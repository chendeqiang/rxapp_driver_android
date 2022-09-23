package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import com.mxingo.driver.R
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import javax.inject.Inject


class HybridSearchActivity : BaseActivity() {

    val url: String? = "https://m.ctrip.com/webapp/hybrid/schedule/search.html?navBarStyle=gray"
    val urlTemp: String = "https://m.ctrip.com/webapp/hybrid/schedule"
    private lateinit var webView: WebView

    @Inject
    lateinit var present: MyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hybrid_search)
        webView = findViewById(R.id.wv) as WebView
        webView.loadUrl(url)
        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.setSupportZoom(false)
        webSetting.domStorageEnabled = true
        webSetting.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
        webView.webChromeClient = MyWebChromeClient()
        webView.webViewClient = MyWebViewClient()
        ComponentHolder.appComponent!!.inject(this)
        present.register(this)
    }


    inner class MyWebChromeClient : WebChromeClient() {
        //跳转手机浏览器
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        // 在android 7.0系统以上 已经摒弃了shouldOverrideUrlLoading(WebView view, String url)此方法，
        // 所以，如果要拦截URL，我们需要做兼容性处理，
        // 重写shouldOverrideUrlLoading(WebView view, WebResourceRequest request)方法，获取得到的可正常使用的URL
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.url.toString())
            } else {
                view.loadUrl(request.toString())
            }
            return true

//            if (url.contains(urlTemp)) {
//                view.loadUrl(url)
//                return true
//            } else {
//                if (url.equals("https://m.ctrip.com/html5/flight")) {
//                    this@HybridSearchActivity.finish()
//                } else {
//                    view.loadUrl(this@HybridSearchActivity.url)
//                }
//                return false
//            }
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            if (view.url.contains("https://")) {
                handler.proceed()
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
        }
    }

    companion object Factory {
        @JvmStatic
        fun startHybridSearchActivity(context: Context) {
            val intent = Intent(context, HybridSearchActivity::class.java)
            context.startActivity(intent)
        }
    }

}



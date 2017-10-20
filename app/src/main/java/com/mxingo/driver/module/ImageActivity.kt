package com.mxingo.driver.module

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mxingo.driver.R
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TextUtil

class ImageActivity : BaseActivity() {

    private var url = ""
    private var uri: Uri? = null
    private var index = 0

    private lateinit var imgBig: ImageView
    private lateinit var btnCancel: Button

    companion object {
        const val requestCode = 1000
        @JvmStatic
        fun startImageActivity(activity: Activity, uri: Uri?, url: String, index: Int, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, ImageActivity::class.java)
                    .putExtra(Constants.URL, url)
                    .putExtra(Constants.URI, uri)
                    .putExtra(Constants.INDEX, index), requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "查看图片"

        imgBig = findViewById(R.id.img_big) as ImageView
        btnCancel = findViewById(R.id.btn_cancel) as Button

        url = intent.getStringExtra(Constants.URL)
        uri = intent.getParcelableExtra(Constants.URI)
        index = intent.getIntExtra(Constants.INDEX, 0)

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(Constants.INDEX, index))
            finish()
        }

        if (TextUtil.isEmpty(url) && uri != null) {
            imgBig.setImageURI(uri)
        } else if (TextUtil.isEmpty(url) && uri == null) {
            btnCancel.visibility = View.GONE
            finish()
        } else if (!TextUtil.isEmpty(url) && uri == null) {
            Glide.with(this).load(url).into(imgBig)
            btnCancel.visibility = View.GONE
        } else if (!TextUtil.isEmpty(url) && uri != null) {
            imgBig.setImageURI(uri)
        }
    }
}

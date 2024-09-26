package com.mxingo.driver.module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.github.gzuliyujiang.wheelpicker.DatePicker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.impl.UnitDateFormatter
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import com.github.gzuliyujiang.wheelpicker.widget.OptionWheelLayout
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog
import com.mxingo.driver.dialog.SelectImageTypeDialog
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.model.QiNiuTokenEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.module.upload.UploadFile
import com.mxingo.driver.module.upload.UploadFileResult
import com.mxingo.driver.utils.BitmapUtil
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by chendeqiang on 2018/4/14 12:43
 */
class DriverRegisterActivity : BaseActivity(){
    private lateinit var etDriverName: EditText
    private lateinit var etDriverMobile: EditText
    private lateinit var etCarNumber: EditText
    private lateinit var etDriverId: EditText
    private lateinit var etDriverNumber: EditText
    private lateinit var tvDrivrerBirth: TextView
    private lateinit var tvDrivrerAddress: TextView
    private lateinit var tvDrivingModel: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var rlDrivrerBirth: RelativeLayout
    private lateinit var rlDrivrerAddress: RelativeLayout
    private lateinit var rlDrivingModel: RelativeLayout
    private lateinit var rlEndDate: RelativeLayout
    private lateinit var btnNextRegister: Button
    //btn_next_register

    private var bitFront: Bitmap? = null
    private var bitBack: Bitmap? = null
    private var bitDriverFront: Bitmap? = null
    private var bitDriverBack: Bitmap? = null
    private lateinit var imgTmp: ImageView
    private lateinit var imgUpload: Array<ImageView>
    private var imgFrontUri: Uri? = null
    private var imgBackUri: Uri? = null
    private var imgDriverFrontUri: Uri? = null
    private var imgDriverBackUri: Uri? = null

    private var keyFront: String = ""
    private var keyBack: String = ""
    private var keyDriverFront: String = ""
    private var keyDriverBack: String = ""

    private var urlFront: String = ""
    private var urlBack: String = ""
    private var urlDriverFront: String = ""
    private var urlDriverBack: String = ""
    private var cameraFile: File? = null
    private var outputUri: Uri? = null
    private val upload = UploadFile()

    private val drivingModel = arrayListOf("A1", "A2", "A3", "B1", "B2", "C1")
    private var dataEmpty = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var status = -1

    @Inject
    lateinit var presenter: MyPresenter

    private lateinit var progress: MyProgress

    private var token = ""


    //private lateinit var drivingModelPicker: OnePicker
    private lateinit var drivingModelPicker: OptionPicker
    private lateinit var birthPicker: DatePicker
    private lateinit var wheelLayout: DateWheelLayout
    private lateinit var drivingModelWheelLayout: OptionWheelLayout


    companion object {
        const val START_CAMERA = 101
        const val PERMISSION_CAMERA = 101
        const val PERMISSION_ALBUM = 102
        const val START_ALBUM = 102
        const val CROP = 100
        const val REQUEST_IMAGE = 1000

        @JvmStatic
        fun startRegisterActivity(context: Context) {
            context.startActivity(Intent(context, DriverRegisterActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "司机注册信息"
        etDriverName = findViewById(R.id.et_driver_name) as EditText
        etDriverMobile = findViewById(R.id.et_driver_mobile) as EditText
        tvDrivrerBirth = findViewById(R.id.tv_driver_birthday) as TextView
        tvDrivrerAddress = findViewById(R.id.tv_driver_address) as TextView
        tvDrivingModel = findViewById(R.id.tv_driving_model) as TextView
        etCarNumber = findViewById(R.id.et_car_number) as EditText
        etDriverId = findViewById(R.id.et_driver_id) as EditText
        etDriverNumber = findViewById(R.id.et_driver_number) as EditText
        tvEndDate = findViewById(R.id.tv_end_date) as TextView
        btnNextRegister = findViewById(R.id.btn_next_register) as Button

        rlDrivrerBirth = findViewById(R.id.rl_driver_birthday) as RelativeLayout
        rlDrivrerAddress = findViewById(R.id.rl_driver_address) as RelativeLayout
        rlDrivingModel = findViewById(R.id.rl_driving_model) as RelativeLayout
        rlEndDate = findViewById(R.id.rl_end_date) as RelativeLayout

        //drivingModelPicker = OnePicker(this, drivingModel)
        drivingModelPicker= OptionPicker(this)
        drivingModelWheelLayout =drivingModelPicker.wheelLayout
        drivingModelPicker.setBodyWidth(240)
        drivingModelPicker.setData(drivingModel)
        drivingModelPicker.setDefaultPosition(0)
        drivingModelWheelLayout.setCurvedEnabled(true)
        drivingModelWheelLayout.setCurtainEnabled(true)
        drivingModelWheelLayout.setCurtainColor(0x33CCFF)
        drivingModelWheelLayout.setIndicatorEnabled(true)
        drivingModelWheelLayout.setIndicatorColor(0x33CCFF)
        drivingModelWheelLayout.setIndicatorSize(getResources().getDisplayMetrics().density * 2)
        drivingModelWheelLayout.setTextColor(0x33CCFF)


        birthPicker = DatePicker(this)
        wheelLayout=birthPicker.wheelLayout
        birthPicker.setBodyWidth(240)
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY)
        wheelLayout.setDateFormatter(UnitDateFormatter())
        wheelLayout.setRange(DateEntity.target(1918, 1, 1), DateEntity.target(2010, 12, 31), DateEntity.today())
        wheelLayout.setCurtainEnabled(true)
        wheelLayout.setCurtainColor(0x33CCFF)
        wheelLayout.setIndicatorEnabled(true)
        wheelLayout.setIndicatorColor(0x33CCFF)
        wheelLayout.setIndicatorSize(getResources().getDisplayMetrics().density * 2)
        wheelLayout.setTextColor(0x33CCFF)
        wheelLayout.getYearLabelView().setTextColor(0x33CCFF)
        wheelLayout.getMonthLabelView().setTextColor(0x33CCFF)
        wheelLayout.setResetWhenLinkage(false);

        imgUpload = arrayOf(findViewById(R.id.img_id_front) as ImageView,
                findViewById(R.id.img_id_back) as ImageView,
                findViewById(R.id.img_driver_front) as ImageView,
                findViewById(R.id.img_driver_back) as ImageView)

        btnNextRegister.setOnClickListener {

            CarRegisterActivity.startCarRegisterActivity(this)

            progress.show()
            presenter.getQiNiuToken()
        }

        rlDrivrerBirth.setOnClickListener {
            birthPicker.show()
        }
        birthPicker.setOnDatePickedListener { year, month, day ->
            tvDrivrerBirth.text = "$year-$month-$day"
        }

        rlDrivingModel.setOnClickListener {
            drivingModelPicker.show()
        }
        drivingModelPicker.setOnOptionPickedListener { position, item ->
            tvDrivingModel.text = item.toString()
            dataEmpty[4] = 1
            checkView()
        }
//        drivingModelPicker.setOnPickListener {
//            tvDrivingModel.text = drivingModelPicker.selectedItem
//            dataEmpty[4] = 1
//            checkView()
//        }


        imgUpload[0].setOnClickListener {
            if (bitFront == null) {
                imgTmp = imgUpload[0]
                showImageDialog()
            } else {
                LogUtils.d("url", urlFront)
                ImageActivity.startImageActivity(this, imgFrontUri, urlFront, 0, REQUEST_IMAGE)
            }
        }
        imgUpload[1].setOnClickListener {
            if (bitBack == null) {
                imgTmp = imgUpload[1]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgBackUri, urlBack, 1, REQUEST_IMAGE)
            }
        }
        imgUpload[2].setOnClickListener {
            if (bitDriverFront == null) {
                imgTmp = imgUpload[2]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgDriverFrontUri, urlDriverFront, 2, REQUEST_IMAGE)
            }
        }
        imgUpload[3].setOnClickListener {
            if (bitDriverBack == null) {
                imgTmp = imgUpload[3]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgDriverBackUri, urlDriverBack, 3, REQUEST_IMAGE)
            }
        }
    }

    private fun checkView() {
        if (dataEmpty[0] + dataEmpty[1] + dataEmpty[2] + dataEmpty[3] + dataEmpty[4] + dataEmpty[5] + dataEmpty[6] + dataEmpty[7] + dataEmpty[8] + dataEmpty[9] + dataEmpty[10] == 11) {
            btnNextRegister.isSelected = true
            btnNextRegister.isClickable = true
        } else {
            btnNextRegister.isSelected = false
            btnNextRegister.isClickable = false
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        when {
            any::class == QiNiuTokenEntity::class -> getQiNiuToken(any)
            any::class == CommEntity::class -> checkInfo(any)
        }
    }

    private fun checkInfo(any: Any) {
        val data = any as CommEntity
        if (data.rspCode == "00") {
            ShowToast.showCenter(this, "提交成功")
            finish()
        } else {
            ShowToast.showCenter(this, data.rspDesc)
        }
        progress.dismiss()
    }

    private fun getQiNiuToken(any: Any) {
        val data = any as QiNiuTokenEntity
        if (data.rspCode == "00") {
            token = data.qiniuToken
            LogUtils.d("token", token)
            handler.sendEmptyMessage(2)
        } else {
            progress.dismiss()
        }
    }

    private fun showImageDialog() {
        val dialog = SelectImageTypeDialog(this)
        dialog.setOnAlbumClickListener {
            val intent = Intent("android.intent.action.PICK")
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), START_ALBUM)
        }

        dialog.setOnCameraClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    val message = MessageDialog(this)
                    message.setMessageText("您的权限申请失败，请前往应用信息打开相机权限")
                    message.setOnOkClickListener {
                        val packageURI = Uri.parse("package:" + this@DriverRegisterActivity.packageName)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                        startActivity(intent)
                        message.dismiss()
                    }
                    message.show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
                }

            }
        }
        dialog.show()
    }

    private fun startCamera() {
        if (cameraFile == null) {
            cameraFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getPhotoFileName())
        }
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 指定调用相机拍照后照片的储存路径
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (Build.VERSION.SDK_INT >= 24) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, this.packageName + ".fileprovider", cameraFile!!))
        } else {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile!!))
        }
        startActivityForResult(cameraIntent, DriverCarRegistrationActivity.START_CAMERA)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPhotoFileName(): String {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss")
        return dateFormat.format(date) + ".png"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA && grantResults[0] == 0) {
            startCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (START_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            outputUri = Uri.fromFile(cameraFile)
            handler.sendEmptyMessage(1)
        } else if (START_ALBUM == requestCode && resultCode == Activity.RESULT_OK) {
            if (data!!.data != null) {
                outputUri = data.data
                handler.sendEmptyMessage(1)
            }
        } else if (REQUEST_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
            clearIndexImage(data!!.getIntExtra(Constants.INDEX, 0))
        }
    }

    private fun clearIndexImage(index: Int) {
        imgUpload[index].setImageResource(R.drawable.ic_add)
        when (index) {
            0 -> {
                bitFront = null
                imgFrontUri = null
            }
            1 -> {
                bitBack = null
                imgBackUri = null
            }
            2 -> {
                bitDriverFront = null
                imgDriverFrontUri = null
            }
            3 -> {
                bitDriverBack = null
                imgDriverBackUri = null
            }
        }
    }

    private val handler = Handler(Handler.Callback {
        if (it.what == 1) {
            imgTmp.setImageBitmap(BitmapUtil.compressUri(this, outputUri, CROP, CROP))
            when (imgTmp) {
                imgUpload[0] -> {
                    bitFront = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgFrontUri = outputUri
                    dataEmpty[7] = 1
                    checkView()
                }
                imgUpload[1] -> {
                    bitBack = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgBackUri = outputUri
                    dataEmpty[8] = 1
                    checkView()
                }

                imgUpload[2] -> {
                    bitDriverFront = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgDriverFrontUri = outputUri
                    dataEmpty[9] = 1
                    checkView()
                }

                imgUpload[3] -> {
                    bitDriverBack = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgDriverBackUri = outputUri
                    dataEmpty[10] = 1
                    checkView()
                }
            }
        } else if (it.what == 2) {
            progress.setLabel("上传中")
            progress.setDetailsLabel("身份证正面上传中...")
            keyFront = getKey()
            upload(bitFront!!, keyFront, token, 0)
        } else if (it.what == 3) {
            progress.setDetailsLabel("身份证反面上传中...")
            keyBack = getKey()
            upload(bitBack!!, keyBack, token, 1)
        } else if (it.what == 4) {
            progress.setDetailsLabel("驾驶员证正面上传中...")
            keyDriverFront = getKey()
            upload(bitDriverFront!!, keyDriverFront, token, 2)
        } else if (it.what == 5) {
            progress.setDetailsLabel("驾驶员证反面上传中...")
            keyDriverBack = getKey()
            upload(bitDriverBack!!, keyDriverBack, token, 3)
        } else if (it.what == 6) {
            progress.dismiss()
            ShowToast.showCenter(this, "上传完成")
            progress.setLabel("请稍等")
            progress.setDetailsLabel("请求中")
        } else if (it.what == 7) {
            progress.dismiss()
        }
        true
    })

    private fun getKey(): String {
        return "rxdriver/${System.currentTimeMillis()}.png"
    }

    private fun upload(bitmap: Bitmap, key: String, token: String, index: Int) {
        Thread(Runnable {
            upload.uploadFileByte(
                    BitmapUtil.compressImage(bitmap),
                    key,
                    token,
                    UploadResult(index)
            )
        }).start()
    }

    inner class UploadResult(val index: Int) : UploadFileResult {
        override fun uploadSuccess() {
            this@DriverRegisterActivity.handler.sendEmptyMessage(index + 3)
        }

        override fun uploadFail() {
            this@DriverRegisterActivity.handler.sendEmptyMessage(7)
        }

        override fun uploadProgress(percent: Double) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}
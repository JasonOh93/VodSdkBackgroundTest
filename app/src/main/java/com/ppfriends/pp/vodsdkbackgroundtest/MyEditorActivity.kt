package com.ppfriends.pp.vodsdkbackgroundtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.android.pesdk.assets.filter.basic.ColorFilterAssetBW
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.FilterSettings
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.backend.model.state.ProgressState
import ly.img.android.pesdk.backend.model.state.manager.StateHandler
import ly.img.android.pesdk.ui.activity.VideoEditorActivity
import ly.img.android.pesdk.ui.widgets.ImgLyFloatSlider
import ly.img.android.pesdk.ui.widgets.ProgressView
import ly.img.android.pesdk.ui.widgets.SeekSlider

class MyEditorActivity : VideoEditorActivity() {

//    private var myView: MyAdsView? = null

    var TAG = MyEditorActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        myView = findViewById(R.id.my_ads_view)
        // do something with it if you need to

        Toast.makeText(this, "Toast from custom Activity", Toast.LENGTH_SHORT).show()
    }

    override fun onCloseClicked() {
        // Override Editor close behavior, to remove dialog
        val stateHandler = stateHandler
        val loadSettings = stateHandler[LoadSettings::class]
        val result = EditorSDKResult.Builder(EditorSDKResult.Status.CANCELED).also {
            it.setProduct(stateHandler.product)
            it.setSourceUri(loadSettings.source)
            it.setSettingsList(stateHandler.createSettingsListDump())
        }
        setResult(result)
        Log.e(TAG, "onCloseClicked: ")
        finish()
    }

    override fun onExportStart(stateHandler: StateHandler) {
        // Change settings before starting export.
//        stateHandler[FilterSettings::class].filter = ColorFilterAssetBW()
        Log.e(TAG, "onExportStart: ")
//        val intent2 = Intent(this, MainActivity2::class.java)
//        intent2.putExtra("aaa", "aaa")
//        startActivity(intent2)
        super.onExportStart(stateHandler)

        Pro()
    }

    override fun onSaveClicked() {
        super.onSaveClicked()
        Toast.makeText(this, "save clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onExportDone(result: EditorSDKResult): Boolean {

        Log.e(TAG, "onExportDone: ${result.resultUri}")

        return super.onExportDone(result)
    }

    inner class Pro : ProgressView(this) {

        var isFirst = true

        override fun onExportProgressChanged(state: ProgressState?) {
            super.onExportProgressChanged(state)

            if(state != null){
                if(state.isExportRunning){
//                    val progress: String =
//                        ((state.exportProgress * 1000.0f).toInt().toFloat() / 10.0f).toString() + "%"
//                    Log.e(
//                        TAG,
//                        "onExportProgressChanged: getExportProgress ${progress}"
//                    )
                    if(isFirst && state.exportProgress > 0.0f) {
//                        var intent2: Intent = Intent(this@MyEditorActivity, EditUpLoadVideoActivity::class.java)
//                        startActivity(intent2)

                        isFirst = false
                    } else {
                        val progress: Float =
                            ((state.exportProgress * 1000.0f).toInt().toFloat() / 10.0f)

//                        val intent = Intent(Global.ACTION_VOD_SDK_ENCODING)
//                        intent.putExtra(Global.VOD_SDK_ENCODING_PROGRESS_FLOAT, progress)
//                        sendBroadcast(intent)
                    }
                    Log.e(TAG, "onExportProgressChanged: state.exportProgress ${state.exportProgress}", )
                }
            }
        }
    }

}
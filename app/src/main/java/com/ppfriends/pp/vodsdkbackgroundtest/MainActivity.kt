package com.ppfriends.pp.vodsdkbackgroundtest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.ppfriends.pp.vodsdkbackgroundtest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic
import ly.img.android.pesdk.assets.font.basic.FontPackBasic
import ly.img.android.pesdk.backend.decoder.ImageSource
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.config.CropAspectAsset
import ly.img.android.pesdk.backend.model.constant.OutputMode
import ly.img.android.pesdk.backend.model.state.*
import ly.img.android.pesdk.backend.model.state.manager.SettingsList
import ly.img.android.pesdk.backend.operator.headless.DocumentRenderWorker
import ly.img.android.pesdk.backend.views.abstracts.ImgLyUIRelativeContainer
import ly.img.android.pesdk.ui.activity.ImgLyActivity
import ly.img.android.pesdk.ui.activity.VideoEditorActivity
import ly.img.android.pesdk.ui.activity.VideoEditorBuilder
import ly.img.android.pesdk.ui.model.state.*
import ly.img.android.pesdk.ui.panels.AdjustmentToolPanel
import ly.img.android.pesdk.ui.panels.item.AdjustOption
import ly.img.android.pesdk.ui.panels.item.CropAspectItem
import ly.img.android.pesdk.ui.panels.item.ToolItem
import ly.img.android.pesdk.ui.widgets.EditorRootView
import ly.img.android.pesdk.ui.widgets.ImgLyFloatSlider
import ly.img.android.pesdk.ui.widgets.ProgressView
import ly.img.android.pesdk.ui.widgets.SeekSlider
import ly.img.android.serializer._3.IMGLYFileWriter
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){

    var binding : ActivityMainBinding? = null

    val TAG = MainActivity::class.java.simpleName

    private var player: ExoPlayer? = null
//    private val playerListener = PlayerStateListener()

    companion object {
        const val STORAGE_PERMISSION_REQUEST_CODE = 111
        const val VESDK_RESULT = 1
        const val CAMERA_RESULT = 2
        const val GALLERY_RESULT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.button.setOnClickListener {
            if (setStoragePermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                if (setStoragePermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    onAlbum()
                }
            }
        }


        binding!!.button2.setOnClickListener {
            if (setStoragePermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                if (setStoragePermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    onAlbum()
                }
            }
        }

    }

    private fun setAaa(uri: Uri?) {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        if (player == null) {
            player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
            binding!!.pvVodDetailVideo.player = player
            // video center zoom
            binding!!.pvVodDetailVideo.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        player?.apply {

//            setMediaItems(videoMediaItems)
//            var vodStrimmingLink =
//                "https://d2zihajmogu5jn.cloudfront.net/bipbop-advanced/bipbop_16x9_variant.m3u8"

            setMediaItem(MediaItem.fromUri(uri!!))
//            addListener(playerListener)

//            var thumb = ThumbnailUtils.createVideoThumbnail()

            prepare()
//            play()
            playWhenReady = true
//            seekTo(2000L)

            // 무한 재생
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    private fun onAlbum(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, GALLERY_RESULT)
    }

    fun setStoragePermission(activity: Activity, permission: String): Boolean {
        return if (ActivityCompat.checkSelfPermission(activity, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(permission),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(permission),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            }
            false
        } else {
            true
        }
    }

    fun openEditor(inputSource: Uri?) {
        val settingsList =
            createVESDKSettingsList(inputSource)

        settingsList.configure<LoadSettings> {
            it.source = inputSource
        }

        settingsList.configure<AssetConfig> {
            it.getAssetMap(CropAspectAsset::class.java)
                .clear().add(
                    CropAspectAsset("aspect_1_1", 1, 1, false),
                )
        }
//
//// Add your own Asset to UI config and select the Force crop Mode.
        settingsList.configure<UiConfigAspect> {
            it.setAspectList(
                CropAspectItem("aspect_1_1"),
            )
            it.forceCropMode = UiConfigAspect.ForceCrop.SHOW_TOOL_ALWAYS
        }

        settingsList.configure<TransformSettings> {
            it.setForceCrop("aspect_1_1", "aspect_1_1")
        }

//        settingsList.setEventTracker(CoustomEventTracker())

        VideoEditorBuilder(this, MyEditorActivity::class.java)
            .setSettingsList(settingsList)
            .startActivityForResult(this, VESDK_RESULT)

        settingsList.release()

//        WorkManager.getInstance(this)
//            .getWorkInfosByTagLiveData(DocumentRenderWorker.DEFAULT_WORK_INFO_TAG)
//            .observe(this){
//                it.forEach { job ->
//                    Log.e("IMG.LY", "StateState: ${job.state.isFinished}", )
//                    Log.e("IMG.LY", "State: ${job.state} Progress: ${job.progress.getFloat(DocumentRenderWorker.FLOAT_PROGRESS_KEY, 2f)}")
//                    Log.e("IMG.LY", "job.state.name : ${job.state.name}")
//                    Log.e("IMG.LY", "job : ${job}")
//                }
//            }

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            WorkManager.getInstance(this)
//                .getWorkInfosByTagLiveData(DocumentRenderWorker.DEFAULT_WORK_INFO_TAG)
//                .observe(this){
//                    it.forEach { job ->
//                        Log.e("IMG.LY", "State: ${job.state} Progress: ${job.progress.getFloat(DocumentRenderWorker.FLOAT_PROGRESS_KEY, 1f)}")
//                    }
//                }
//        }
    }

    private fun createVESDKSettingsList(inputSource: Uri?) =
        VideoEditorSettingsList(true)
            .configure<UiConfigMainMenu> {
                it.toolList
                    .removeAll(
                        listOf<ToolItem>(
                            ToolItem("imgly_tool_overlay", R.string.pesdk_overlay_title_name, ImageSource.create(R.drawable.imgly_icon_tool_overlay)),
                            ToolItem(
                                "imgly_tool_text_design",
                                R.string.pesdk_textDesign_title_name,
                                ImageSource.create(R.drawable.imgly_icon_tool_text_design)
                            ),
                            ToolItem("imgly_tool_frame", R.string.pesdk_frame_title_name, ImageSource.create(R.drawable.imgly_icon_tool_frame))
                        )
                    )
            }
            .configure<UiConfigFilter> {
                it.setFilterList(FilterPackBasic.getFilterPack())
            }
            .configure<UiConfigText> {
                it.setFontList(FontPackBasic.getFontPack())
            }
            .configure<UiConfigAdjustment> {
                it.optionList.removeAll(
                    listOf<AdjustOption>(
                        AdjustOption(
                            AdjustmentToolPanel.OPTION_CLARITY,
                            R.string.pesdk_adjustments_button_clarityTool,
                            ImageSource.create(R.drawable.imgly_icon_option_clarity)
                        ),
                        AdjustOption(
                            AdjustmentToolPanel.OPTION_GAMMA,
                            R.string.pesdk_adjustments_button_gammaTool,
                            ImageSource.create(R.drawable.imgly_icon_option_gamma)
                        ),
                        AdjustOption(
                            AdjustmentToolPanel.OPTION_BLACKS,
                            R.string.pesdk_adjustments_button_blacksTool,
                            ImageSource.create(R.drawable.imgly_icon_option_blacks)
                        ),
                        AdjustOption(
                            AdjustmentToolPanel.OPTION_WHITES,
                            R.string.pesdk_adjustments_button_whitesTool,
                            ImageSource.create(R.drawable.imgly_icon_option_whites)
                        )
                    )
                )
            }
            .configure<TrimSettings> {
                it.setMinimumVideoLength(2L, TimeUnit.SECONDS)
                it.setMaximumVideoLength(60L, TimeUnit.SECONDS)
            }
            .configure<VideoEditorSaveSettings> {

//                if(inputSource != null) {
//                    val fileDescriptor: AssetFileDescriptor? = contentResolver.openAssetFileDescriptor(inputSource, "r")
//                    Log.e(
//                        TAG,
//                        "createVESDKSettingsList: fileDescriptor : ${fileDescriptor?.length}"
//                    )
//                    if (fileDescriptor?.length ?: 0L > 500000000L) {
//                        Log.e(TAG, "createVESDKSettingsList: fileDescriptor : 500MB")
//                        // 300MB
//                        it.bitsPerPixel = 0.03f
//                    }else if (fileDescriptor?.length ?: 0L > 300000000L) {
//                        Log.e(TAG, "createVESDKSettingsList: fileDescriptor : 300MB")
//                        // 300MB
//                        it.bitsPerPixel = 0.04f
//                    }else if(fileDescriptor?.length ?: 0L > 30000000L){
//                        Log.e(TAG, "createVESDKSettingsList:  // 30MB")
//                        // 30MB
//                        it.bitsPerPixel = 0.14f
//                    }
//                    fileDescriptor?.close()
//                }
//                it.outputName = "VOD_asdfasdf" +
//                        "_${System.currentTimeMillis()}"

//                it.outputMode = OutputMode.EXPORT_IF_NECESSARY
                it.outputMode = OutputMode.EXPORT_ONLY_SETTINGS_LIST
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        intent ?: return

        if (resultCode == AppCompatActivity.RESULT_OK && (requestCode == CAMERA_RESULT || requestCode == GALLERY_RESULT)) {
            // VideoEditer -> Use ImgLy
            // Open Editor with some uri in this case with an video selected from the system gallery.

            val selectedVideo = intent?.data
            if (selectedVideo != null) {
                openEditor(selectedVideo)
            }

            Log.e(TAG, "onActivityResult: Source video :: ${intent?.data}")

        }else if(resultCode == AppCompatActivity.RESULT_CANCELED && (requestCode == CAMERA_RESULT || requestCode == GALLERY_RESULT)){
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == VESDK_RESULT) {
            // Editor has saved an Video.

            val result = EditorSDKResult(intent)

            Log.e("VESDK", "Source video is located here ${result.sourceUri}")
            Log.e("VESDK", "Result video is located here ${result.resultUri}")



            ///////////////1111111111111111111111111111111111111111111111111111111111///////////////////////
            when (result.resultStatus) {
                EditorSDKResult.Status.CANCELED -> Toast.makeText(this, "Editor cancelled", Toast.LENGTH_SHORT).show()
                EditorSDKResult.Status.DONE_WITHOUT_EXPORT -> {
                    // Export the video in background using WorkManager
                    result.settingsList.use { document ->
                        val aaa = DocumentRenderWorker.createWorker(document)
                        WorkManager.getInstance(this).enqueue(aaa)
                        Log.e(
                            TAG,
                            "onActivityResult: VESDK : DONE_WITHOUT_EXPORT ${document}"
                        )

                        WorkManager.getInstance(this)
                            .getWorkInfoByIdLiveData(aaa.id)
                            .observe(this){
                                Log.e("IMG.LY", "StateState: ${it.state.isFinished}", )
                                Log.e("IMG.LY", "State: ${it.state} Progress: ${it.progress.getFloat(DocumentRenderWorker.FLOAT_PROGRESS_KEY, 2f)}")
                                Log.e("IMG.LY", "job.state.name : ${it.state.name}")
                                Log.e("IMG.LY", "job : ${it}")
                            }
//                        test(document)
                        setAaa(result.resultUri)
                    }
                }
                EditorSDKResult.Status.EXPORT_DONE -> {
                    result.settingsList.release()
//                    val intent2 = Intent(this, MainActivity2::class.java)
//                    intent2.putExtra("aaa", result.resultUri.toString())
//                    startActivity(intent2)
                    Log.e(TAG, "onActivityResult: converting end!!!!!!!!!!!!!!!!!")
                }
                else -> {
                }
            }

//            result.settingsList.release()


            return
            /////////////////1111111111111111111111111111111111111111111111111111111111/////////////////////

        } else if (resultCode == AppCompatActivity.RESULT_CANCELED && requestCode == VESDK_RESULT) {
            // Editor was canceled
            val data = EditorSDKResult(intent)

            val sourceURI = data.sourceUri
            // TODO: Do something with the source...
        }
    }

    private fun test(settingsList : SettingsList) {
        VideoEditorBuilder(this)
            .setSettingsList(settingsList)
            .startActivityForResult(this, 999999)

        settingsList.release()
    }

}
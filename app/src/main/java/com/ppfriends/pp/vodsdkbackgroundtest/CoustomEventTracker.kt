package com.ppfriends.pp.vodsdkbackgroundtest

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import ly.img.android.pesdk.annotations.OnEvent
import ly.img.android.pesdk.backend.model.state.LayerListSettings
import ly.img.android.pesdk.backend.model.state.ProgressState
import ly.img.android.pesdk.backend.model.state.layer.ImageStickerLayerSettings
import ly.img.android.pesdk.backend.model.state.manager.EventTracker
import ly.img.android.pesdk.ui.panels.*

class CoustomEventTracker : EventTracker, Parcelable {

    private val map = hashMapOf<Class<out AbstractToolPanel>, String>()

    constructor() : super()

    constructor(parcel: Parcel) : super(parcel)

    init {
        map[MenuToolPanel::class.java] = "menu"
        map[VideoCompositionToolPanel::class.java] = "video composition"
        map[VideoCompositionTrimToolPanel::class.java] = "video composition trim"
        map[VideoLibraryToolPanel::class.java] = "video library"
        map[VideoTrimToolPanel::class.java] = "video trim"
        map[AudioOverlayOptionsToolPanel::class.java] = "audio overlay"
        map[AudioGalleryToolPanel::class.java] = "audio gallery"
        map[TransformToolPanel::class.java] = "transform"
        map[FilterToolPanel::class.java] = "filter"
        map[AdjustmentToolPanel::class.java] = "adjust"
        map[FocusToolPanel::class.java] = "focus"
        map[StickerToolPanel::class.java] = "sticker"
        map[StickerOptionToolPanel::class.java] = "sticker option"
        map[ColorOptionStickerInkToolPanel::class.java] = "sticker color ink"
        map[ColorOptionStickerTintToolPanel::class.java] = "sticker color tint"
        map[TextToolPanel::class.java] = "text"
        map[TextOptionToolPanel::class.java] = "text option"
        map[TextFontOptionToolPanel::class.java] = "text font"
        map[ColorOptionTextBackgroundToolPanel::class.java] = "text color bg"
        map[ColorOptionTextForegroundToolPanel::class.java] = "text color fg"
        map[BrushToolPanel::class.java] = "brush"
        map[ColorOptionBrushToolPanel::class.java] = "brush color"
    }

    /*
 * This annotated method tracks when the export starts
 */
    @OnEvent(ProgressState.Event.EXPORT_START)
    fun onExportStarted(settings: LayerListSettings) {
        // log all the added sticker ids
        settings.layerSettingsList.forEach {
            (it as? ImageStickerLayerSettings)?.stickerConfig?.id?.also {
                Log.e("Catalog", it)
            }
        }
    }

    @OnEvent(ProgressState.Event.EXPORT_PROGRESS)
    fun onExportProgressed(state: ProgressState?) {
        val progress: String =
            (((state?.exportProgress ?: 0f) * 1000.0f).toInt().toFloat() / 10.0f).toString() + "%"
        Log.e("STATES", "onExportProgressed: state : ${progress}")
    }

    @OnEvent(ProgressState.Event.EXPORT_FINISH)
    fun onExportProgressEnd(state: ProgressState?) {
        val progress: String =
            (((state?.exportProgress ?: 0f) * 1000.0f).toInt().toFloat() / 10.0f).toString() + "%"
        Log.e("EXPORT_FINISH", "onExportProgressEnd: state : ${progress}")
    }

    companion object CREATOR : Parcelable.Creator<CoustomEventTracker> {
        override fun createFromParcel(parcel: Parcel): CoustomEventTracker {
            return CoustomEventTracker(parcel)
        }

        override fun newArray(size: Int): Array<CoustomEventTracker?> {
            return arrayOfNulls(size)
        }
    }
}
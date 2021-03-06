package tk.zwander.lockscreenwidgets.util

import android.annotation.IntegerRes
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import androidx.collection.ArraySet
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import tk.zwander.lockscreenwidgets.R
import tk.zwander.lockscreenwidgets.data.WidgetData
import tk.zwander.lockscreenwidgets.data.WidgetSizeData

/**
 * Handle data persistence.
 */
class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        const val KEY_CURRENT_WIDGETS = "current_widgets"
        const val KEY_FRAME_WIDTH = "frame_width"
        const val KEY_NOTIFICATION_FRAME_WIDTH = "notification_frame_width"
        const val KEY_FRAME_HEIGHT = "frame_height"
        const val KEY_NOTIFICATION_FRAME_HEIGHT = "notification_frame_height"
        const val KEY_POS_X = "position_x"
        const val KEY_NOTIFICATION_POS_X = "notification_position_x"
        const val KEY_POS_Y = "position_y"
        const val KEY_NOTIFICATION_POS_Y = "notification_position_y"
        const val KEY_FIRST_VIEWING = "first_viewing"
        const val KEY_FIRST_RUN = "first_run"
        const val KEY_HIDE_ON_NOTIFICATIONS = "hide_on_notifications"
        const val KEY_WIDGET_FRAME_ENABLED = "widget_frame_enabled"
        const val KEY_PAGE_INDICATOR_BEHAVIOR = "page_indicator_behavior"
        const val KEY_HIDE_ON_SECURITY_PAGE = "hide_on_security_page"
        const val KEY_HIDE_ON_NOTIFICATION_SHADE = "hide_on_notification_shade"
        const val KEY_ANIMATE_SHOW_HIDE = "animate_show_hide"
        const val KEY_CURRENT_PAGE = "current_page"
        const val KEY_DEBUG_LOG = "debug_log"
        const val KEY_FRAME_COL_COUNT = "frame_col_count"
        const val KEY_FRAME_ROW_COUNT = "frame_row_count"
        const val KEY_SHOW_DEBUG_ID_VIEW = "show_debug_id_view"
        const val KEY_ACCESSIBILITY_EVENT_DELAY = "accessibility_event_delay"
        const val KEY_PRESENT_IDS = "present_ids"
        const val KEY_NON_PRESENT_IDS = "non_present_ids"
        const val KEY_WIDGET_SIZES = "widget_sizes"
        const val KEY_SHOW_IN_NOTIFICATION_CENTER = "show_in_notification_center"
        const val KEY_SHOW_ON_MAIN_LOCK_SCREEN = "show_on_main_lock_screen"
        const val KEY_FRAME_CORNER_RADIUS = "corner_radius"
        const val KEY_FRAME_BACKGROUND_COLOR = "background_color"
        const val KEY_FRAME_MASKED_MODE = "masked_mode"
        const val KEY_TOUCH_PROTECTION = "touch_protection"
        const val KEY_REQUEST_UNLOCK = "request_unlock"

        const val VALUE_PAGE_INDICATOR_BEHAVIOR_HIDDEN = 0
        const val VALUE_PAGE_INDICATOR_BEHAVIOR_AUTO_HIDE = 1
        const val VALUE_PAGE_INDICATOR_BEHAVIOR_SHOWN = 2

        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: run {
                instance = PrefManager(context.applicationContext)
                instance!!
            }
        }
    }

    //The actual SharedPreferences implementation
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val gson = GsonBuilder()
        .create()

    //The widgets currently added to the widget frame
    var currentWidgets: LinkedHashSet<WidgetData>
        get() = gson.fromJson(
            getString(KEY_CURRENT_WIDGETS),
            object : TypeToken<LinkedHashSet<WidgetData>>() {}.type
        ) ?: LinkedHashSet()
        set(value) {
            putString(
                KEY_CURRENT_WIDGETS,
                gson.toJson(value)
            )
        }

    //IDs the user has selected. The widget frame will hide if any of these are detected on-screen.
    var presentIds: HashSet<String>
        get() = HashSet(getStringSet(KEY_PRESENT_IDS, HashSet())!!)
        set(value) {
            putStringSet(
                KEY_PRESENT_IDS,
                value)
        }

    //IDs the user has selected. The widget frame will hide if any of these are *not* detected on-screen.
    var nonPresentIds: HashSet<String>
        get() = HashSet(getStringSet(KEY_NON_PRESENT_IDS, HashSet())!!)
        set(value) {
            putStringSet(
                KEY_NON_PRESENT_IDS,
                value
            )
        }

    //If any widgets have custom sizes, those sizes are stored here.
    var widgetSizes: HashMap<Int, WidgetSizeData>
        get() = gson.fromJson(
            getString(KEY_WIDGET_SIZES),
            object : TypeToken<HashMap<Int, WidgetSizeData>>() {}.type
        ) ?: HashMap()
        set(value) {
            putString(
                KEY_WIDGET_SIZES,
                gson.toJson(value)
            )
        }

    //The width of the frame in DP
    var frameWidthDp: Float
        get() = getFloat(KEY_FRAME_WIDTH, getResourceFloat(R.integer.def_frame_width))
        set(value) {
            putFloat(KEY_FRAME_WIDTH, value)
        }

    //The width of the frame in the NC in DP.
    var notificationFrameWidthDp: Float
        get() = getFloat(KEY_NOTIFICATION_FRAME_WIDTH, getResourceFloat(R.integer.def_notification_frame_width))
        set(value) {
            putFloat(KEY_NOTIFICATION_FRAME_WIDTH, value)
        }

    //The height of the frame in DP
    var frameHeightDp: Float
        get() = getFloat(KEY_FRAME_HEIGHT, getResourceFloat(R.integer.def_frame_height))
        set(value) {
            putFloat(KEY_FRAME_HEIGHT, value)
        }

    //The height of the frame in the NC in DP.
    var notificationFrameHeightDp: Float
        get() = getFloat(KEY_NOTIFICATION_FRAME_HEIGHT, getResourceFloat(R.integer.def_notification_frame_height))
        set(value) {
            putFloat(KEY_NOTIFICATION_FRAME_HEIGHT, value)
        }

    //The horizontal position of the center of the frame (from the center of the screen) in pixels
    var posX: Int
        get() = getInt(KEY_POS_X, 0)
        set(value) {
            putInt(KEY_POS_X, value)
        }

    //The horizontal position of the center of the frame in the NC (from the center of the screen) in pixels
    var notificationPosX: Int
        get() = getInt(KEY_NOTIFICATION_POS_X, calculateNCPosXFromRightDefault())
        set(value) {
            putInt(KEY_NOTIFICATION_POS_X, value)
        }

    //The vertical position of the center of the frame (from the center of the screen) in pixels
    var posY: Int
        get() = getInt(KEY_POS_Y, 0)
        set(value) {
            putInt(KEY_POS_Y, value)
        }

    //The vertical position of the center of the frame in the NC (from the center of the screen) in pixels
    var notificationPosY: Int
        get() = getInt(KEY_NOTIFICATION_POS_Y, calculateNCPosYFromTopDefault())
        set(value) {
            putInt(KEY_NOTIFICATION_POS_Y, value)
        }

    //The current page/index of the frame the user is currently on. Stored value may be higher
    //than the last index of the current widgets, so make sure to guard against that.
    var currentPage: Int
        get() = getInt(KEY_CURRENT_PAGE, 0)
        set(value) {
            putInt(KEY_CURRENT_PAGE, value)
        }

    //The timeout between Accessibility events being reported to Lockscreen Widgets.
    //Lower values mean faster responses, at the cost of battery life and reliability.
    var accessibilityEventDelay: Int
        get() = getInt(KEY_ACCESSIBILITY_EVENT_DELAY, 50)
        set(value) {
            putInt(KEY_ACCESSIBILITY_EVENT_DELAY, value)
        }

    //If it's the first time the user's seeing the widget frame. Used to decide
    //if the interaction hint should be shown.
    var firstViewing: Boolean
        get() = getBoolean(KEY_FIRST_VIEWING, true)
        set(value) {
            putBoolean(KEY_FIRST_VIEWING, value)
        }

    //If it's the first time the user's running the app. Used to decided if
    //the full intro sequence should be shown.
    var firstRun: Boolean
        get() = getBoolean(KEY_FIRST_RUN, true)
        set(value) {
            putBoolean(KEY_FIRST_RUN, value)
        }

    //Whether or not the widget frame should hide when there are > min priority
    //notifications shown.
    var hideOnNotifications: Boolean
        get() = getBoolean(KEY_HIDE_ON_NOTIFICATIONS, false)
        set(value) {
            putBoolean(KEY_HIDE_ON_NOTIFICATIONS, value)
        }

    //Whether the widget frame is actually enabled.
    var widgetFrameEnabled: Boolean
        get() = getBoolean(KEY_WIDGET_FRAME_ENABLED, true)
        set(value) {
            putBoolean(KEY_WIDGET_FRAME_ENABLED, value)
        }

    //Whether the widget frame should hide on the password/pin/fingerprint/pattern
    //input screen.
    var hideOnSecurityPage: Boolean
        get() = getBoolean(KEY_HIDE_ON_SECURITY_PAGE, false)
        set(value) {
            putBoolean(KEY_HIDE_ON_SECURITY_PAGE, value)
        }

    //Whether the widget frame should hide when the notification shade is down.
    var hideOnNotificationShade: Boolean
        get() = getBoolean(KEY_HIDE_ON_NOTIFICATION_SHADE, false)
        set(value) {
            putBoolean(KEY_HIDE_ON_NOTIFICATION_SHADE, value)
        }

    //Whether the widget frame should animate its hide/show sequences.
    var animateShowHide: Boolean
        get() = getBoolean(KEY_ANIMATE_SHOW_HIDE, true)
        set(value) {
            putBoolean(KEY_ANIMATE_SHOW_HIDE, value)
        }

    //Whether to create verbose logs for debugging purposes.
    var debugLog: Boolean
        get() = getBoolean(KEY_DEBUG_LOG, false)
        set(value) {
            putBoolean(KEY_DEBUG_LOG, value)
        }

    //Whether to show the debug ID list overlay on the widget frame.
    //Only true if debugLog is true.
    var showDebugIdView: Boolean
        get() = getBoolean(KEY_SHOW_DEBUG_ID_VIEW, false) && debugLog
        set(value) {
            putBoolean(KEY_SHOW_DEBUG_ID_VIEW, value)
        }

    //How the page indicator (scrollbar) should behave (always show, fade out on inactivity, never show).
    var pageIndicatorBehavior: Int
        get() = getString(KEY_PAGE_INDICATOR_BEHAVIOR, VALUE_PAGE_INDICATOR_BEHAVIOR_AUTO_HIDE.toString())!!.toInt()
        set(value) {
            putString(KEY_PAGE_INDICATOR_BEHAVIOR, value.toString())
        }

    //The background color of the widget frame.
    var backgroundColor: Int
        get() = getInt(KEY_FRAME_BACKGROUND_COLOR, Color.TRANSPARENT)
        set(value) {
            putInt(KEY_FRAME_BACKGROUND_COLOR, value)
        }

    //Whether masked mode is enabled.
    //On compatible devices with a proper wallpaper setup,
    //this will emulate a transparent widget background by
    //drawing the user's wallpaper as the frame background.
    var maskedMode: Boolean
        get() = getBoolean(KEY_FRAME_MASKED_MODE, false)
        set(value) {
            putBoolean(KEY_FRAME_MASKED_MODE, value)
        }

    //How many columns of widgets should be shown per page.
    var frameColCount: Int
        get() = getInt(KEY_FRAME_COL_COUNT, 1)
        set(value) {
            putInt(KEY_FRAME_COL_COUNT, value)
        }

    //How many rows of widgets should be shown per page.
    var frameRowCount: Int
        get() = getInt(KEY_FRAME_ROW_COUNT, 1)
        set(value) {
            putInt(KEY_FRAME_ROW_COUNT, value)
        }

    //Whether to show in the notification center.
    //This mode has separate dimensions and positioning
    //vs the standard lock screen mode.
    //Only available for Samsung One UI 1.0 and later.
    var showInNotificationCenter: Boolean
        get() = getBoolean(KEY_SHOW_IN_NOTIFICATION_CENTER, false)
        set(value) {
            putBoolean(KEY_SHOW_IN_NOTIFICATION_CENTER, value)
        }

    //A dependent option for [showInNotificationCenter].
    //Disabling this while [showInNotificationCenter] is enabled
    //will cause the widget frame to only show in the notification center.
    var showOnMainLockScreen: Boolean
        get() = getBoolean(KEY_SHOW_ON_MAIN_LOCK_SCREEN, true) || !showInNotificationCenter
        set(value) {
            putBoolean(KEY_SHOW_ON_MAIN_LOCK_SCREEN, value)
        }

    //The corner radius for the widget frame
    //(how rounded the corners are, in dp)
    var cornerRadiusDp: Float
        get() = getInt(KEY_FRAME_CORNER_RADIUS, resources.getInteger(R.integer.def_corner_radius_dp_scaled_10x)) / 10f
        set(value) {
            putInt(KEY_FRAME_CORNER_RADIUS, (value * 10f).toInt())
        }

    //Whether to enable touch protection
    //(ignore touches when proximity sensor
    //is covered)
    var touchProtection: Boolean
        get() = getBoolean(KEY_TOUCH_PROTECTION, false)
        set(value) {
            putBoolean(KEY_TOUCH_PROTECTION, value)
        }

    //Whether to request an unlock/dismiss the
    //notification center when LSWidg detects
    //an Activity launch.
    var requestUnlock: Boolean
        get() = getBoolean(KEY_REQUEST_UNLOCK, true)
        set(value) {
            putBoolean(KEY_REQUEST_UNLOCK, value)
        }

    fun getCorrectFrameWidth(isNC: Boolean): Float {
        return if (isNC) notificationFrameWidthDp
        else frameWidthDp
    }

    fun setCorrectFrameWidth(isNC: Boolean, width: Float) {
        if (isNC) notificationFrameWidthDp = width
        else frameWidthDp = width
    }

    fun getCorrectFrameHeight(isNC: Boolean): Float {
        return if (isNC) notificationFrameHeightDp
        else frameHeightDp
    }

    fun setCorrectFrameHeight(isNC: Boolean, height: Float) {
        if (isNC) notificationFrameHeightDp = height
        else frameHeightDp = height
    }

    fun getCorrectFrameX(isNC: Boolean): Int {
        return if (isNC) notificationPosX
        else posX
    }

    fun setCorrectFrameX(isNC: Boolean, x: Int) {
        if (isNC) notificationPosX = x
        else posX = x
    }

    fun getCorrectFrameY(isNC: Boolean): Int {
        return if (isNC) notificationPosY
        else posY
    }

    fun setCorrectFrameY(isNC: Boolean, y: Int) {
        if (isNC) notificationPosY = y
        else posY = y
    }

    fun getString(key: String, def: String? = null) = prefs.getString(key, def)
    fun getFloat(key: String, def: Float) = prefs.getFloat(key, def)
    fun getInt(key: String, def: Int) = prefs.getInt(key, def)
    fun getBoolean(key: String, def: Boolean) = prefs.getBoolean(key, def)
    fun getStringSet(key: String, def: Set<String>) = prefs.getStringSet(key, def)

    fun putString(key: String, value: String?) = prefs.edit { putString(key, value) }
    fun putFloat(key: String, value: Float) = prefs.edit { putFloat(key, value) }
    fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }
    fun putBoolean(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }
    fun putStringSet(key: String, value: Set<String>) = prefs.edit { putStringSet(key, value) }

    fun getResourceFloat(@IntegerRes resource: Int): Float {
        return resources.getInteger(resource).toFloat()
    }
}
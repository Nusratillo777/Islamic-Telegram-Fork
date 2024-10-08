package org.telegram.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.telegram.messenger.FileLog
import org.telegram.messenger.components.local.Prefs
import org.telegram.ui.ActionBar.BackDrawable
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.HeaderCell
import org.telegram.ui.Cells.TextCell
import org.telegram.ui.Components.PermissionRequest
import org.telegram.ui.Components.TableLayout.LayoutParams
import smd.telegram.islamic.R

class PrayNotificationsActivity : AppCompatActivity() {
    var btnBack: ImageView? = null
    var holder1: LinearLayout? = null
    var visibleDialog:Dialog? = null
    fun showDialog(
        dialog: Dialog,
        onDismissListener: DialogInterface.OnDismissListener?
    ): Dialog? {

        try {
            if (visibleDialog != null) {
                visibleDialog?.dismiss()
                visibleDialog = null
            }
        } catch (e: java.lang.Exception) {
            FileLog.e(e)
        }
        try {
            visibleDialog = dialog
            visibleDialog?.setCanceledOnTouchOutside(true)
            visibleDialog?.setOnDismissListener(DialogInterface.OnDismissListener { dialog1: DialogInterface ->
                onDismissListener?.onDismiss(dialog1)
                if (dialog1 === visibleDialog) {
                    visibleDialog = null
                }
            })
            visibleDialog?.show()
            return visibleDialog
        } catch (e: java.lang.Exception) {
            FileLog.e(e)
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun askForPermissons(alert: Boolean) {
        val activity: Activity = this
        val permissons = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= 33 && NotificationPermissionDialog.shouldAsk(activity)) {
            if (alert) {
                showDialog(
                    NotificationPermissionDialog(
                    activity, !PermissionRequest.canAskPermission(Manifest.permission.POST_NOTIFICATIONS)
                ) { granted: Boolean ->
                    if (granted) {
                        activity.requestPermissions(
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }
                }, null)
                return
            }
            permissons.add(Manifest.permission.POST_NOTIFICATIONS)
        }


        if (permissons.isEmpty()) {
            return
        }
        val items = permissons.toTypedArray<String>()
        try {
            activity.requestPermissions(items, 1)
        } catch (ignore: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pray_notifications)
        supportActionBar?.hide()
        findViewById<LinearLayout>(R.id.topView).setBackgroundColor(getThemedColor(Theme.key_actionBarDefault))

        btnBack = findViewById(R.id.btnBack)
        btnBack?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon))

        holder1 = findViewById(R.id.holder1)
        holder1?.removeAllViews()
        val headerCell = HeaderCell(this).apply {
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
            setText("Pray times")
        }
        holder1?.addView(headerCell)
        addMethod("Fajr",0)
        addMethod("Sunrise",1)
        addMethod("Dhuhr",2)
        addMethod("Asr",3)
        addMethod("Maghrib",4)
        addMethod("Isha",5)
    }
    fun addMethod(name:String,id:Int){
        val method1 = LinearLayout(this).apply {
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL

            val textCell = TextCell(this@PrayNotificationsActivity).apply {
                setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
                setText(name, true)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }
            addView(textCell)
            val img = (ImageView(context).apply {
                setPadding(16.px,0,16.px,0)
                setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText))
                setImageResource(if (Prefs.prayNotificationsBlockList.contains(name)) R.drawable.msg_folders_muted else R.drawable.msg_notifications)
            })
            addView(img)
            setOnClickListener {
                askForPermissons(true)
                val list = Prefs.prayNotificationsBlockList
                Prefs.prayNotificationsBlockList = if (list.contains("$name,") || list.contains(name)){
                    list.replace("$name,","").replace(name,"")
                }else{
                    "$list$name,"
                }
                img.setImageResource(if (Prefs.prayNotificationsBlockList.contains(name)) R.drawable.msg_folders_muted else R.drawable.msg_notifications)

            }

        }
        holder1?.addView(method1)
    }

    fun setItemsColor(color: Int) {
        if (btnBack != null) {
            val drawable: Drawable? = btnBack?.getDrawable()
            if (drawable is BackDrawable) {
                drawable.setRotatedColor(color)
            } else if (drawable is BitmapDrawable) {
                btnBack?.setColorFilter(
                    PorterDuffColorFilter(
                        color,
                        PorterDuff.Mode.SRC_IN
                    )
                )
            }
        }
    }
}
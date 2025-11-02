package com.example.umelec

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Date
import java.util.concurrent.TimeUnit
import android.text.format.DateFormat
import androidx.core.content.res.ResourcesCompat

/**
 * Utility class containing reusable logic for managing and displaying the notification dropdown.
 */
class NotificationManager(private val activity: AppCompatActivity) {

    // --- STATE AND DATA ---
    private var isNotificationDropdownVisible = false
    private var popupWindow: PopupWindow? = null

    // NEW: Load the custom typeface once for efficiency
    private val poppinsRegularTypeface: Typeface? by lazy {
        try {
            // NOTE: Assumes you have a file named 'poppins_regular.ttf' in your 'res/font' directory.
            ResourcesCompat.getFont(activity, R.font.poppins_regular)
        } catch (e: Exception) {
            // Fallback to null if the resource is not found
            null
        }
    }

    // 💡 BACKEND/DATABASE GUIDE: This is simulated data.
    private val notifications = mutableListOf(
        NotificationItem(
            id = "n1",
            title = "Election Reminder",
            previewText = "The voting period will officially end in 3 days. Cast your vote now!",
            type = NotificationType.REMINDER,
            timestamp = Date().time - TimeUnit.MINUTES.toMillis(15),
            isRead = false
        ),
        NotificationItem(
            id = "n2",
            title = "Vote Submitted Successfully",
            previewText = "Your ballot was successfully submitted on October 22, 2025.",
            type = NotificationType.SUBMISSION,
            timestamp = Date().time - TimeUnit.HOURS.toMillis(3),
            isRead = true
        ),
        NotificationItem(
            id = "n3",
            title = "System Update",
            previewText = "A minor security patch has been deployed. Check the FAQ for details.",
            type = NotificationType.GENERIC,
            timestamp = Date().time - TimeUnit.DAYS.toMillis(1),
            isRead = false
        ),
        NotificationItem(
            id = "n4",
            title = "New Candidate",
            previewText = "Alice Williams has been added to the Treasurer race.",
            type = NotificationType.GENERIC,
            timestamp = Date().time - TimeUnit.DAYS.toMillis(5),
            isRead = false
        )
    )

    // --- PUBLIC API ---

    /**
     * Toggles the visibility of the notification dropdown menu.
     */
    fun toggleNotificationDropdown(anchorView: ImageView) {
        if (isNotificationDropdownVisible) {
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            popupWindow?.dismiss()
        } else {
            showNotificationDropdown(anchorView)
            isNotificationDropdownVisible = true
        }
    }

    // --- PRIVATE IMPLEMENTATION ---

    /**
     * Creates and displays the custom notification dropdown menu with the new design.
     */
    private fun showNotificationDropdown(anchorView: ImageView) {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.notification_dropdown, null)

        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow?.setOnDismissListener {
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            isNotificationDropdownVisible = false
            popupWindow = null
        }

        anchorView.setColorFilter(Color.parseColor("#FCBE6A"))

        val notificationContainer: LinearLayout = popupView.findViewById(R.id.notificationListContainer)
        val noNotificationText: TextView = popupView.findViewById(R.id.noNotificationTextView)

        // FONT APPLICATION: Apply font to static text view
        noNotificationText.typeface = poppinsRegularTypeface

        notificationContainer.removeAllViews()

        // 💡 CHANGE HERE: Limit to the 3 newest notifications
        val itemsToShow = notifications.sortedByDescending { it.timestamp }.take(3)

        if (itemsToShow.isEmpty()) {
            noNotificationText.visibility = View.VISIBLE
        } else {
            noNotificationText.visibility = View.GONE

            itemsToShow.forEachIndexed { index, item ->
                val notificationItemView = createNotificationItemView(item)
                notificationContainer.addView(notificationItemView)

                // LOGIC FOR SEPARATORS: Add a separator after every item except the last one
                if (index < itemsToShow.size - 1) {
                    val separator = createSeparatorView(activity)
                    notificationContainer.addView(separator)
                }
            }
        }

        // Close Button
        val closeButton: ImageView = popupView.findViewById(R.id.closeDropdownButton)
        closeButton.setOnClickListener { popupWindow?.dismiss() }

        // View All Button (Navigation to Notification.kt)
        val viewAllButton: TextView = popupView.findViewById(R.id.viewAllButton)
        // FONT APPLICATION: Apply font to View All button
        viewAllButton.typeface = poppinsRegularTypeface
        viewAllButton.setOnClickListener {
            val intent = Intent(activity, Notification::class.java)
            activity.startActivity(intent)
            popupWindow?.dismiss()
        }

        // The current offset is -300, which you mentioned is working with your XML setup.
        popupWindow?.showAsDropDown(anchorView, -300, 0)
    }

    /**
     * Dynamically creates the view for a single notification item based on its read status and type.
     */
    private fun createNotificationItemView(item: NotificationItem): View {
        val context = activity

        // Define LayoutParams
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // FIX: Add horizontal margin only for the background-colored (unread) item to avoid corner clipping
        if (!item.isRead) {
            // Note: I've kept your margin at 3.toPx() (3dp)
            val cornerRadiusMargin = 3.toPx()
            lp.leftMargin = cornerRadiusMargin
            lp.rightMargin = cornerRadiusMargin
        }


        // 1. Root Layout
        val rootView = LinearLayout(context).apply {
            id = View.generateViewId()
            layoutParams = lp // Assign the potentially modified layout params
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            // Set padding here so text is indented (inner padding)
            setPadding(16.toPx(), 8.toPx(), 16.toPx(), 8.toPx())

            if (!item.isRead) {
                // Set background color
                setBackgroundColor(Color.parseColor("#ECF1F4"))
            } else {
                setBackgroundColor(Color.TRANSPARENT)
            }

            setOnClickListener {
                // 💡 BACKEND/DATABASE GUIDE: Update the 'isRead' status for this notification ID in your backend here.
                val intent = Intent(context, Notification2::class.java)
                intent.putExtra("NOTIFICATION_ID", item.id)
                context.startActivity(intent)
                popupWindow?.dismiss()
            }
        }

        // 2. Icon (Left side)
        val iconRes = when (item.type) {
            NotificationType.REMINDER -> R.drawable.ic_notif_reminder
            NotificationType.SUBMISSION -> R.drawable.ic_notif_submitted
            // Using a standard Android system icon as the generic fallback
            NotificationType.GENERIC -> android.R.drawable.ic_menu_info_details
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(30.toPx(), 30.toPx()).apply {
                marginEnd = 12.toPx()
            }
            setImageResource(iconRes)
            setColorFilter(Color.parseColor("#4A4A68"))
            contentDescription = "Notification type icon"
        }
        rootView.addView(icon)

        // 3. Text Container (Title, Preview, Time)
        val textContainer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            orientation = LinearLayout.VERTICAL
        }
        rootView.addView(textContainer)

        // 3a. Title
        val titleText = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            text = item.title
            textSize = 16f
            // FONT APPLICATION: Apply font and bold style
            setTypeface(poppinsRegularTypeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#4A4A68"))
        }
        textContainer.addView(titleText)

        // 3b. Preview Text (Max 2 lines)
        val previewText = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            text = item.previewText
            textSize = 12f
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            // FONT APPLICATION: Apply font
            typeface = poppinsRegularTypeface
            setTextColor(Color.parseColor("#8C8CA1"))
        }
        textContainer.addView(previewText)

        // 3c. Time/Date
        val timeText = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            text = formatNotificationTime(item.timestamp)
            textSize = 10f
            // FONT APPLICATION: Apply font
            typeface = poppinsRegularTypeface
            setTextColor(Color.parseColor("#8C8CA1"))
        }
        textContainer.addView(timeText)

        // 4. Unread Indicator (ic_circle)
        if (!item.isRead) {
            val unreadIndicator = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(10.toPx(), 10.toPx()).apply {
                    marginStart = 8.toPx()
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }
                setImageResource(R.drawable.ic_circle)
                setColorFilter(Color.parseColor("#4A4A68"))
                contentDescription = "Unread Indicator"
            }
            rootView.addView(unreadIndicator)
        }

        return rootView
    }

    /**
     * Creates a full-width separator line view.
     */
    private fun createSeparatorView(context: Context): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.toPx() // 1dp height
            )
            // Use a dark gray color for internal separators
            setBackgroundColor(Color.parseColor("#4A4A68"))
        }
    }


    /**
     * Formats the timestamp into "X mins ago" or "Oct 11, 2025".
     */
    private fun formatNotificationTime(timestamp: Long): CharSequence {
        val now = System.currentTimeMillis()
        val difference = now - timestamp

        // If less than 24 hours ago, show relative time (e.g., 10 minutes ago)
        return if (difference < TimeUnit.DAYS.toMillis(1)) {
            // Uses RelativeTimeSpanString for "X minutes ago"
            DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS)
        } else {
            // Otherwise, show absolute date (e.g., Oct 11, 2025)
            DateFormat.format("MMM dd, yyyy", timestamp)
        }
    }


    // Utility extension function to convert DP to pixels
    private fun Int.toPx(): Int = (this * activity.resources.displayMetrics.density).toInt()
}
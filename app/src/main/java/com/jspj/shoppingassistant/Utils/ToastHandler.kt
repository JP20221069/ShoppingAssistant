package com.jspj.shoppingassistant.Utils

import android.content.Context
import android.os.Handler
import android.widget.Toast


/**
 * A class for showing a `Toast` from background processes using a
 * `Handler`.
 *
 * @author kaolick
 */
class ToastHandler(// General attributes
    private val mContext: Context
) {
    private val mHandler: Handler

    /**
     * Class constructor.
     *
     * @param _context
     * The `Context` for showing the `Toast`
     */
    init {
        mHandler = Handler()
    }

    /**
     * Runs the `Runnable` in a separate `Thread`.
     *
     * @param _runnable
     * The `Runnable` containing the `Toast`
     */
    private fun runRunnable(_runnable: Runnable) {
        var thread: Thread? = object : Thread() {
            override fun run() {
                mHandler.post(_runnable)
            }
        }
        thread!!.start()
        thread.interrupt()
        thread = null
    }

    /**
     * Shows a `Toast` using a `Handler`. Can be used in
     * background processes.
     *
     * @param _resID
     * The resource id of the string resource to use. Can be
     * formatted text.
     * @param _duration
     * How long to display the message. Only use LENGTH_LONG or
     * LENGTH_SHORT from `Toast`.
     */
    fun showToast(_resID: Int, _duration: Int) {
        val runnable = Runnable { // Get the text for the given resource ID
            val text = mContext.resources.getString(_resID)
            Toast.makeText(mContext, text, _duration).show()
        }
        runRunnable(runnable)
    }

    /**
     * Shows a `Toast` using a `Handler`. Can be used in
     * background processes.
     *
     * @param _text
     * The text to show. Can be formatted text.
     * @param _duration
     * How long to display the message. Only use LENGTH_LONG or
     * LENGTH_SHORT from `Toast`.
     */
    fun showToast(_text: CharSequence?, _duration: Int) {
        val runnable =
            Runnable { Toast.makeText(mContext, _text, _duration).show() }
        runRunnable(runnable)
    }
}
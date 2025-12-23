package com.yourapp.volumenormalizer

import android.content.Context

/**
 * Simple wrapper around SharedPreferences for storing user configurable values.
 * Maintains sensitivity and restore speed across app launches.
 */
object Prefs {
    private const val PREFS_NAME = "vn_prefs"
    private const val KEY_SENSITIVITY = "sensitivity"
    private const val KEY_RESTORE_SPEED = "restore_speed"

    /** Get the saved sensitivity or a default value. */
    fun getSensitivity(ctx: Context): Float =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getFloat(KEY_SENSITIVITY, 2.2f)

    /** Persist the sensitivity to preferences. */
    fun setSensitivity(ctx: Context, value: Float) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putFloat(KEY_SENSITIVITY, value).apply()
    }

    /** Get the saved restore speed or a default value. */
    fun getRestoreSpeed(ctx: Context): Float =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getFloat(KEY_RESTORE_SPEED, 0.65f)

    /** Persist the restore speed to preferences. */
    fun setRestoreSpeed(ctx: Context, value: Float) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putFloat(KEY_RESTORE_SPEED, value).apply()
    }
}
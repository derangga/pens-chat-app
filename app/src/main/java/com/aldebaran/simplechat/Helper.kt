package com.aldebaran.simplechat

import android.content.Intent
import androidx.fragment.app.FragmentActivity

inline fun <reified T: FragmentActivity> FragmentActivity.goToActivity(){
    Intent(this, T::class.java)
        .also { startActivity(it) }
}
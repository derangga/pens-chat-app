package com.aldebaran.simplechat.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {

    val loginState by lazy { MutableLiveData<Status>() }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun isLogin(): Boolean{
        return auth.currentUser != null
    }

    fun doLogin(email: String, password: String){
        loginState.value = Status.LOADING
        auth.signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { task ->
            loginState.value = if(task.isSuccessful){
                Status.SUCCESS
            } else Status.ERROR
        }
    }
}
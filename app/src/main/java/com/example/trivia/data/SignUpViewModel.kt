package com.example.trivia.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trivia.util.LoadingState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel:ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)
    fun signUp(email:String,password:String)=viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            val user = FirebaseAuth.getInstance().currentUser

            user?.sendEmailVerification()
            loadingState.emit(LoadingState.LOADED)

        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }
}